# 389 Directory Server Password Policy Test Setup

> **This is a test setup for development and debugging only. Do not use in production.** Passwords are stored in plain text, TLS is not configured, and access controls are minimal.

Test environment for the LDAP password policy control ([`draft-behera-ldap-password-policy`](https://datatracker.ietf.org/doc/html/draft-behera-ldap-password-policy-11)).
Exercises both elements of the `PasswordPolicyResponseValue`:

- **`warning [0]`**: `timeBeforeExpiration` — password expires in ~24 hours
- **`error [1]`**: `changeAfterReset(2)` — user must change password

Having both elements present in the response exercises the full parsing path
in Keycloak's `PasswordPolicyControl`: the warning's constructed `[0]` wrapper
must be correctly skipped so the following `error [1]` element is found.

## Prerequisites

- Docker and Docker Compose
- `ldap-utils` (Fedora/RHEL: `sudo dnf install openldap-clients`, Debian/Ubuntu: `sudo apt install ldap-utils`)

## Usage

### Start

```bash
docker compose up -d --build
./setup.sh
```

### Re-arm password change

After testing, reset the test user's password back to `changeme` and re-enable the forced password change:

```bash
ldapmodify -x -H ldap://localhost:3389 -D "cn=Directory Manager" -w admin1234 <<'EOF'
dn: uid=testuser,ou=people,dc=example,dc=org
changetype: modify
replace: userPassword
userPassword: changeme
EOF
```

### Stop

```bash
docker compose down -v
```

## What the setup does

1. Starts 389 Directory Server (Fedora 42 based image) on port 3389
2. Creates a manager account with `write` access to `userPassword` and `read` access to the directory
3. Enables the global password policy: `passwordMustChange: on`, `passwordExp: on`, `passwordMaxAge: 86400`, `passwordWarning: 86401`, `passwordSendExpiringTime: on`
4. Creates a test user and sets their password to `changeme`

The manager account is created before the password policy is enabled so that its
password is not flagged for change.

## Expected behavior

1. Log in as `testuser` with password `changeme`. Keycloak redirects to the **Update Password** page.
2. Enter a new password. After the update, login completes normally.
3. Log out and log in again with the new password. No password change is required — login succeeds directly.

To repeat the test, re-arm the forced password change (see above).

## Gotchas

### Directory Manager triggers passwordMustChange automatically

When Directory Manager changes a user's password, 389ds automatically marks it
as requiring change on the next login. There is no need to manually set
`pwdReset: TRUE`.

### passwordSendExpiringTime

Setting `passwordSendExpiringTime: on` makes 389ds always include the
`timeBeforeExpiration` warning in the ppolicy response control, regardless of
the warning period.

### ldappasswd does not work over plain LDAP

389ds rejects the LDAP Password Modify Extended Operation over unencrypted
connections. Use `ldapmodify` to change `userPassword` directly instead
of `ldappasswd`. This applies to the re-arm step as well.

### Secure binds disabled for testing

The setup disables `nsslapd-require-secure-binds` so that password operations
work over plain LDAP. In production, use LDAPS or StartTLS instead.

### Manager account for Keycloak

The manager account is a regular user, not the Directory Manager. Using
Directory Manager as a Keycloak bind DN is bad practice — it has unrestricted
access to the entire directory. The manager has `write` access to `userPassword`
and `read` access to the rest of the tree, configured via ACIs.

## Accounts

| Account | DN | Password |
|---|---|---|
| Directory Manager | `cn=Directory Manager` | `admin1234` |
| Manager | `uid=manager,ou=people,dc=example,dc=org` | `manager` |
| Test user | `uid=testuser,ou=people,dc=example,dc=org` | `changeme` |

## Keycloak LDAP federation settings

See the [Keycloak LDAP federation documentation](https://www.keycloak.org/docs/latest/server_admin/index.html#_ldap) for general setup and the [LDAP password policy section](https://www.keycloak.org/docs/latest/server_admin/index.html#_ldap_password_policy) for enabling password change after reset.

| Setting | Value |
|---|---|
| Edit Mode | `WRITABLE` |
| Vendor | `Red Hat Directory Server` |
| Connection URL | `ldap://localhost:3389` |
| Bind DN | `uid=manager,ou=people,dc=example,dc=org` |
| Bind Credential | `manager` |
| Users DN | `ou=people,dc=example,dc=org` |
| Username LDAP attribute | `uid` |
| Import Users | `ON` |
| Enable LDAP password policy | `ON` |
