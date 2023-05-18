# <span>Keycloak</span> Quickstarts

<span>Keycloak</span> is an Open Source Identity and Access Management solution for modern Applications and Services.

The quickstarts herein provided demonstrate securing applications with <span>Keycloak</span> using different programming languages (and frameworks) 
and how to extend the server capabilities through a set of Java-based [Service Provider Interfaces(SPI)](https://www.keycloak.org/docs/latest/server_development/). 
They provide small, specific, working examples that can be used as a reference for your own project.

They are organized in this repository under different categories (or directories) as follows:

| Category  | Description                                                                           |
|-----------|---------------------------------------------------------------------------------------|
| extension | Examples about how to extend the server capabilities using some of the Keycloak SPIs. |
| jakarta   | Examples about how secure Jakarta Applications                                        |
| js        | Examples about how to secure JavaScript Applications                                  |
| nodejs    | Examples about how to secure NodeJS Applications                                      |
| spring    | Examples about how to secure Spring Applications                                      |

For any missing programming language and framework, you might want to consider looking at:

* **Quarkus**
  * [Securing a Client Application](https://quarkus.io/guides/security-oidc-code-flow-authentication-tutorial)
  * [Securing a Resource Server Application](https://quarkus.io/guides/security-oidc-bearer-token-authentication-tutorial)

We are happy to accept contributions for any reference that demonstrates how to
integrate Keycloak with additional programming languages or frameworks.

## Building, Testing, and Running the Quickstarts

First clone the Keycloak repository:

    git clone https://github.com/keycloak/keycloak-quickstarts.git
    cd keycloak-quickstarts

Each quickstart provides its own documentation with the steps you need to follow in order to build, test, and run the example.
Look at the `README.md` file at the root of a quickstart for more details.

## Help and Documentation

* [Documentation](https://www.keycloak.org/documentation.html)
* [User Mailing List](https://groups.google.com/d/forum/keycloak-user) - Mailing list for help and general questions about Keycloak

## Reporting Security Vulnerabilities

If you've found a security vulnerability, please look at the [instructions on how to properly report it](https://github.com/keycloak/keycloak/security/policy)

## Reporting an issue

If you believe you have discovered a defect in Keycloak, please open [an issue](https://github.com/keycloak/keycloak-quickstarts/issues).
Please remember to provide a good summary, description as well as steps to reproduce the issue.

## Contributing

Before contributing to this repository, please read our [contributing guidelines](CONTRIBUTING.md).

## Related Projects

* [Keycloak](https://github.com/keycloak/keycloak) - Keycloak Server and Java adapters
* [Keycloak Node.js Connect](https://github.com/keycloak/keycloak-nodejs-connect) - Node.js adapter for Keycloak
* [Keycloak Node.js Admin Client](https://github.com/keycloak/keycloak-nodejs-admin-client) - Node.js library for Keycloak Admin REST API

## License

* [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)

