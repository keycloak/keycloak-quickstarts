#!/bin/bash

set -euo pipefail

LDAP_URI="ldap://localhost:389"
ADMIN_DN="cn=admin,dc=example,dc=org"
ADMIN_PW="admin"
MANAGER_DN="uid=manager,ou=users,dc=example,dc=org"
MANAGER_PW="manager"
BASE_DN="dc=example,dc=org"

wait_for_ldap() {
    echo "Waiting for OpenLDAP to be ready..."
    for i in $(seq 1 30); do
        if ldapsearch -x -H "$LDAP_URI" -D "$ADMIN_DN" -w "$ADMIN_PW" -b "$BASE_DN" -s base dn &>/dev/null; then
            echo "OpenLDAP is ready."
            return 0
        fi
        sleep 1
    done
    echo "OpenLDAP did not become ready in time."
    exit 1
}

load_ppolicy_module() {
    echo "Loading ppolicy module..."
    # In OpenLDAP 2.6, loading the ppolicy module also auto-registers the
    # ppolicy schema (pwdPolicy objectclass, pwdAttribute, etc.) — there is
    # no separate schema LDIF to load.
    ldapmodify -x -H "$LDAP_URI" -D "cn=admin,cn=config" -w config <<'EOF'
dn: cn=module{0},cn=config
changetype: modify
add: olcModuleLoad
olcModuleLoad: ppolicy
EOF
}

configure_ppolicy_overlay() {
    echo "Adding ppolicy overlay..."
    # olcPPolicySendResponse was removed in newer OpenLDAP; the overlay now
    # always sends the response when the client includes the control in the
    # request, so we do not set it here.
    ldapadd -x -H "$LDAP_URI" -D "cn=admin,cn=config" -w config <<'EOF'
dn: olcOverlay=ppolicy,olcDatabase={1}mdb,cn=config
objectClass: olcOverlayConfig
objectClass: olcPPolicyConfig
olcOverlay: ppolicy
olcPPolicyDefault: cn=default,ou=policies,dc=example,dc=org
EOF
}

create_policy() {
    echo "Creating ou=policies and default password policy..."
    # pwdExpireWarning is set to pwdMaxAge + 1 so the timeBeforeExpiration
    # warning is included immediately after a password change. The ppolicy
    # overlay uses a strict less-than comparison (timeBeforeExpiration <
    # pwdExpireWarning), so setting them equal would suppress the warning
    # until one second has elapsed.
    ldapadd -x -H "$LDAP_URI" -D "$ADMIN_DN" -w "$ADMIN_PW" <<'EOF'
dn: ou=policies,dc=example,dc=org
objectClass: organizationalUnit
ou: policies

dn: cn=default,ou=policies,dc=example,dc=org
objectClass: pwdPolicy
objectClass: person
objectClass: top
cn: default
sn: default
pwdAttribute: userPassword
pwdMustChange: TRUE
pwdMaxAge: 86400
pwdExpireWarning: 86401
EOF
}

create_users() {
    echo "Creating ou=users, manager account, and test user..."
    # The manager account is used as Keycloak's bind DN. It is a regular user,
    # NOT the rootdn (cn=admin). This matters because the rootdn bypasses
    # ppolicy processing entirely — password changes made by rootdn do not
    # trigger pwdReset, and binds as rootdn never receive ppolicy responses.
    ldapadd -x -H "$LDAP_URI" -D "$ADMIN_DN" -w "$ADMIN_PW" <<'EOF'
dn: ou=users,dc=example,dc=org
objectClass: organizationalUnit
ou: users

dn: uid=manager,ou=users,dc=example,dc=org
objectClass: inetOrgPerson
uid: manager
cn: LDAP Manager
sn: Manager
userPassword: manager

dn: uid=testuser,ou=users,dc=example,dc=org
objectClass: inetOrgPerson
uid: testuser
cn: Test User
sn: User
mail: testuser@example.org
EOF
}

configure_acl() {
    # The manager needs "write" (not "manage") on userPassword. With "manage",
    # Keycloak's password update would re-trigger pwdReset: TRUE, creating an
    # infinite forced-password-change loop (see keycloak/keycloak#15253).
    # The manager also needs "read" on the rest of the tree so Keycloak can
    # search for and import users.
    echo "Granting manager write access to userPassword and read access to the tree..."
    ldapmodify -x -H "$LDAP_URI" -D "cn=admin,cn=config" -w config <<'EOF'
dn: olcDatabase={1}mdb,cn=config
changetype: modify
add: olcAccess
olcAccess: {0}to attrs=userPassword
  by dn.exact="uid=manager,ou=users,dc=example,dc=org" write
  by self write
  by anonymous auth
  by * none
-
add: olcAccess
olcAccess: {1}to *
  by dn.exact="uid=manager,ou=users,dc=example,dc=org" read
  by * break
EOF
}

reset_user_password() {
    echo "Setting testuser password and marking as must-change..."
    ldappasswd -x -H "$LDAP_URI" -D "$ADMIN_DN" -w "$ADMIN_PW" \
        -s "changeme" "uid=testuser,ou=users,dc=example,dc=org"
    # pwdReset must be set explicitly because the rootdn bypasses ppolicy
    # processing — password changes via rootdn do not set pwdReset: TRUE
    # automatically. This applies to both OpenLDAP 2.4.x and 2.6.x.
    ldapmodify -x -H "$LDAP_URI" -D "$ADMIN_DN" -w "$ADMIN_PW" <<'EOF'
dn: uid=testuser,ou=users,dc=example,dc=org
changetype: modify
replace: pwdReset
pwdReset: TRUE
EOF
}

verify() {
    echo ""
    echo "=== Verification ==="
    # Expect both ppolicy response elements:
    #   warning [0]: timeBeforeExpiration (password expires in ~86400s)
    #   error   [1]: changeAfterReset(2) ("Password must be changed")
    # These two elements together exercise the parser path fixed in PR #52403:
    # the warning's constructed [0] wrapper must be skipped so the following
    # error [1] element is correctly identified.
    echo ""
    ldapwhoami -x -H "$LDAP_URI" \
        -D "uid=testuser,ou=users,dc=example,dc=org" -w "changeme" \
        -e ppolicy -v 2>&1 || true
    echo ""
}

wait_for_ldap
load_ppolicy_module
configure_ppolicy_overlay
create_policy
create_users
configure_acl
reset_user_password
verify

echo ""
echo "Done. OpenLDAP is running on $LDAP_URI"
echo "  Root DN:     $ADMIN_DN  (password: $ADMIN_PW)"
echo "  Manager DN:  $MANAGER_DN  (password: $MANAGER_PW) — use as Keycloak bind DN"
echo "  Test user:   uid=testuser,ou=users,$BASE_DN"
echo "  Test pass:   changeme  (must be changed on next login)"
