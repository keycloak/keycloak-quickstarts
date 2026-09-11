# OpenLDAP Password Policy Test Setup

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
ldappasswd -x -H ldap://localhost:389 -D "cn=admin,dc=example,dc=org" -w admin \
    -s "changeme" "uid=testuser,ou=users,dc=example,dc=org"

ldapmodify -x -H ldap://localhost:389 -D "cn=admin,dc=example,dc=org" -w admin <<'EOF'
dn: uid=testuser,ou=users,dc=example,dc=org
changetype: modify
replace: pwdReset
pwdReset: TRUE
EOF
```

### Stop

```bash
docker compose down -v
```

## What the setup does

1. Starts OpenLDAP 2.6 (`debian:trixie` based image) on port 389
2. Loads the `ppolicy` overlay module
3. Creates a default password policy with `pwdMustChange: TRUE`, `pwdMaxAge: 86400`, and `pwdExpireWarning: 86401`
4. Creates a manager account (non-rootdn) with `write` access to `userPassword` and `read` access to the directory
5. Creates a test user and sets their password to `changeme`
6. Sets `pwdReset: TRUE` on the test user so the next bind returns the `changeAfterReset(2)` ppolicy response control

## Expected behavior

1. Log in as `testuser` with password `changeme`. Keycloak redirects to the **Update Password** page.
2. Enter a new password. After the update, login completes normally.
3. Log out and log in again with the new password. No password change is required — login succeeds directly.

To repeat the test, re-arm the forced password change (see above).

## Gotchas

### pwdReset must be set explicitly

OpenLDAP 2.4.x and 2.6.x do not automatically set `pwdReset: TRUE` when the
rootdn changes a user's password. The rootdn bypasses ppolicy entirely, so
password changes via rootdn don't trigger `pwdReset`. The setup script works
around this by setting `pwdReset: TRUE` directly via `ldapmodify`.

### rootdn bypasses ppolicy

`cn=admin,dc=example,dc=org` is the rootdn. The ppolicy overlay ignores rootdn
operations: binds as rootdn never receive ppolicy response controls, and password
changes by rootdn don't trigger `pwdReset`. The manager account exists
specifically to avoid this — Keycloak must bind as the manager, not rootdn.

### Manager needs write, not manage

The manager has `write` (not `manage`) privilege on `userPassword`. With `manage`,
Keycloak's password update would re-trigger `pwdReset: TRUE` on the user, creating
an infinite forced-password-change loop (see [keycloak/keycloak#15253](https://github.com/keycloak/keycloak/pull/15253)).

### pwdExpireWarning must exceed pwdMaxAge

`pwdExpireWarning` is set to `86401` (one second more than `pwdMaxAge: 86400`).
The ppolicy overlay uses a strict less-than comparison
(`timeBeforeExpiration < pwdExpireWarning`) to decide whether to include the
warning. Setting them equal would suppress the `timeBeforeExpiration` warning
until one second has elapsed after the password change.

### olcPPolicySendResponse is gone

The `olcPPolicySendResponse` attribute was removed in newer OpenLDAP versions.
The ppolicy overlay now always sends the response when the client includes the
ppolicy request control. Attempting to set it will fail with
`attribute type undefined`.

## Accounts

| Account | DN | Password |
|---|---|---|
| Root admin | `cn=admin,dc=example,dc=org` | `admin` |
| Manager | `uid=manager,ou=users,dc=example,dc=org` | `manager` |
| Test user | `uid=testuser,ou=users,dc=example,dc=org` | `changeme` |

## Keycloak LDAP federation settings

See the [Keycloak LDAP federation documentation](https://www.keycloak.org/docs/latest/server_admin/index.html#_ldap) for general setup and the [LDAP password policy section](https://www.keycloak.org/docs/latest/server_admin/index.html#_ldap_password_policy) for enabling password change after reset.

| Setting | Value |
|---|---|
| Edit Mode | `WRITABLE` |
| Vendor | `Other` |
| Connection URL | `ldap://localhost:389` |
| Bind DN | `uid=manager,ou=users,dc=example,dc=org` |
| Bind Credential | `manager` |
| Users DN | `ou=users,dc=example,dc=org` |
| Username LDAP attribute | `uid` |
| Import Users | `ON` |
| Enable LDAP password policy | `ON` |
