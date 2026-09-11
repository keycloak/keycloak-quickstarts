#!/bin/bash

set -euo pipefail

LDAP_URI="ldap://localhost:3389"
DM_DN="cn=Directory Manager"
DM_PW="admin1234"
SUFFIX="dc=example,dc=org"
CONTAINER="389ds-ppolicy"

wait_for_ldap() {
    echo "Waiting for 389ds to be ready..."
    for i in $(seq 1 30); do
        if ldapsearch -x -H "$LDAP_URI" -D "$DM_DN" -w "$DM_PW" -b "" -s base vendorVersion 2>/dev/null | grep -q vendorVersion; then
            echo "389ds is ready."
            return 0
        fi
        sleep 1
    done
    echo "389ds did not become ready in time."
    exit 1
}

create_ou_people() {
    echo "Creating ou=people..."
    # The Dockerfile creates the suffix entry (dc=example,dc=org) via
    # dscreate, but we still need the ou=people container for users.
    ldapadd -x -H "$LDAP_URI" -D "$DM_DN" -w "$DM_PW" <<'EOF'
dn: ou=people,dc=example,dc=org
objectClass: organizationalUnit
ou: people
EOF
}

configure_password_policy() {
    echo "Configuring password policy..."
    # passwordSendExpiringTime makes 389ds always include timeBeforeExpiration
    # in the ppolicy response, regardless of the warning period.
    # passwordWarning is set to passwordMaxAge + 1 so the timeBeforeExpiration
    # warning appears immediately after a password change.
    # nsslapd-require-secure-binds is disabled so password operations work
    # over plain LDAP (this is a test setup, not production).
    ldapmodify -x -H "$LDAP_URI" -D "$DM_DN" -w "$DM_PW" <<'EOF'
dn: cn=config
changetype: modify
replace: nsslapd-require-secure-binds
nsslapd-require-secure-binds: off
-
replace: passwordMustChange
passwordMustChange: on
-
replace: passwordExp
passwordExp: on
-
replace: passwordMaxAge
passwordMaxAge: 86400
-
replace: passwordWarning
passwordWarning: 86401
-
replace: passwordSendExpiringTime
passwordSendExpiringTime: on
EOF
}

create_manager() {
    echo "Creating manager account..."
    # The manager account is used as Keycloak's bind DN. Using Directory
    # Manager as a Keycloak bind DN is bad practice — it has unrestricted
    # access to the entire directory.
    ldapadd -x -H "$LDAP_URI" -D "$DM_DN" -w "$DM_PW" <<'EOF'
dn: uid=manager,ou=people,dc=example,dc=org
objectClass: inetOrgPerson
uid: manager
cn: LDAP Manager
sn: Manager
userPassword: manager
EOF
}

add_manager_aci() {
    echo "Granting manager read/write access..."
    # ACIs grant the manager write access to userPassword and read access
    # to the rest of the tree.
    ldapmodify -x -H "$LDAP_URI" -D "$DM_DN" -w "$DM_PW" <<'EOF'
dn: dc=example,dc=org
changetype: modify
add: aci
aci: (targetattr="userPassword")(version 3.0; acl "manager write userPassword"; allow (write,search) userdn="ldap:///uid=manager,ou=people,dc=example,dc=org";)
-
add: aci
aci: (targetattr="*")(version 3.0; acl "manager read all"; allow (read,search,compare) userdn="ldap:///uid=manager,ou=people,dc=example,dc=org";)
EOF
}

create_test_user() {
    echo "Creating test user..."
    ldapadd -x -H "$LDAP_URI" -D "$DM_DN" -w "$DM_PW" <<'EOF'
dn: uid=testuser,ou=people,dc=example,dc=org
objectClass: inetOrgPerson
uid: testuser
cn: Test User
sn: User
mail: testuser@example.org
EOF
}

reset_test_user_password() {
    echo "Setting testuser password (triggers passwordMustChange)..."
    # Directory Manager password resets trigger passwordMustChange
    # automatically — no need to manually set pwdReset: TRUE.
    # Use ldapmodify instead of ldappasswd — 389ds may reject the LDAP
    # Password Modify Extended Operation over plain LDAP.
    ldapmodify -x -H "$LDAP_URI" -D "$DM_DN" -w "$DM_PW" <<'EOF'
dn: uid=testuser,ou=people,dc=example,dc=org
changetype: modify
replace: userPassword
userPassword: changeme
EOF
}

verify() {
    echo ""
    echo "=== Verification ==="
    # Expect both ppolicy response elements:
    #   warning [0]: timeBeforeExpiration (password expires in ~86400s)
    #   error   [1]: changeAfterReset(2) ("Password must be changed")
    echo ""
    ldapwhoami -x -H "$LDAP_URI" \
        -D "uid=testuser,ou=people,$SUFFIX" -w "changeme" \
        -e ppolicy -v 2>&1 || true
    echo ""
}

wait_for_ldap
create_ou_people
create_manager
add_manager_aci
configure_password_policy
create_test_user
reset_test_user_password
verify

echo ""
echo "Done. 389ds is running on $LDAP_URI"
echo "  Directory Manager: $DM_DN  (password: $DM_PW)"
echo "  Manager DN:        uid=manager,ou=people,$SUFFIX  (password: manager) — use as Keycloak bind DN"
echo "  Test user:         uid=testuser,ou=people,$SUFFIX"
echo "  Test pass:         changeme  (must be changed on next login)"
