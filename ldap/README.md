# LDAP Quickstarts

Test setups for LDAP directory servers with password policy (`ppolicy`) enabled, used to exercise Keycloak's LDAP password policy integration.

| Directory Server | Quickstart                          | Host Port | Description                                                                      |
|------------------|-------------------------------------|-----------|----------------------------------------------------------------------------------|
| OpenLDAP 2.6     | [ppolicy](openldap-ppolicy)         | 389       | OpenLDAP with the `ppolicy` overlay, exercising `changeAfterReset` and `timeBeforeExpiration`. |
| 389 Directory Server | [ppolicy](389ds-ppolicy)        | 3389      | 389ds with native password policy, exercising `changeAfterReset` and `timeBeforeExpiration`.   |

Both quickstarts use different host ports so they can run simultaneously.
