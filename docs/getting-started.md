# Getting started

These quickstarts run on <span>WildFly 10</span>.

Prior to running the quickstarts you should read this entire document and have completed the following steps:

* [Start and configure the <span>Keycloak</span> Server](#keycloak)
* [Start and configure the <span>WildFly</span> Server](#wildfly)

Afterwards you should read the README file for the quickstart you would like to deploy. See [examples](#examples) for
a list of the available quickstarts.

If you run into any problems please refer to the [troubleshooting](#troubleshooting) section.


## Use of <span>KEYCLOAK_HOME</span> and <span>WILDFLY_HOME</span> Variables

The quickstart README files use the replaceable value <span>KEYCLOAK_HOME</span> to denote the path to the <span>Keycloak</span> installation and the
value <span>WILDFLY_HOME</span> to denote the path to the <span>WildFly</span> installation. When you encounter this value in a README file, be sure
to replace it with the actual path to your installations.


## System Requirements

The applications these projects produce are designed to be run on <span>WildFly</span> Application Server 10. 
All you need to build these projects is Java 8 (Java SDK 1.8) and Maven 3.8.2 or later.

## <a id="keycloak"></a>Start the <span>Keycloak</span> Server

To start the <span>Keycloak</span> server as a container, run the following command:

   ````
   podman|docker run --name keycloak -p 8180:8180 \
        -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin \
        quay.io/keycloak/keycloak:latest \
        start-dev \
        --http-port 8180 \
        --http-relative-path /auth
   ````

The URL of the <span>Keycloak</span> server will be http://localhost:8180/auth.

## <a id="add-roles-user"></a>Create Roles and User

To be able to use the examples you need to create some roles as well as at least one sample user. To do first this open
the <span>Keycloak</span> admin console ([localhost:8180/admin](http://localhost:8180/admin) or http://&lt;HOSTNAME&gt;:8080/admin) and
login with the admin credentials:

* Username: admin
* Password: admin

Start by creating a user role:

* Select `Roles` from the menu
* Click `Add Role`
* Enter `user` as `Role Name`
* Click `Save`

Next create a user:

* Select `Users` from the menu
* Click `Add user`
* Enter any values you want for the user
* Click `Save`
* Select `Credentials` from the tabs
* Enter a password in `New Password` and `Password Confirmation`
* Click on the toggle to disable `Temporary`
* Click `Reset Password`
* Click `Role Mappings`
* Select `user` under `Available Roles` and click `Add selected`

As an alternative to manually creating the role and user you can use the partial import feature in the admin console and import
the file [config/partial-import.json](../config/partial-import.json) into your realm.

One more step, if you want to access the examples with the admin user you need to add the `user` role to admin user:

* Select `Users` from the menu
* Click `View all users`
* Click `Edit` for admin user
* Click `Role Mappings`
* Select `user` under `Available Roles` and click `Add selected`

## <a id="wildfly"></a>Start and Configure the <span>WildFly</span> Server

Before starting the <span>WildFly</span> Server start by extracting the <span>Keycloak</span> client adapter into it.

For <span>WildFly</span> extract `keycloak-wildfly-adapter-${project.version}.zip` into <span>WILDFLY_HOME</span>.

If you plan to try the SAML examples you also need the SAML <span>WildFly</span> adapter. To do this for <span>WildFly</span>
`keycloak-saml-wildfly-adapter-dist-${project.version}.zip` into <span>WILDFLY_HOME</span>.

The next step is to start <span>WildFly</span> server:

1. Open a terminal and navigate to the root of the <span>WildFly</span> server directory.
2. Use the following command to start the <span>WildFly</span> server:
   ````
   For Linux:   WILDFLY_HOME/bin/standalone.sh
   For Windows: WILDFLY_HOME\bin\standalone.bat
   ````
3. To install the <span>Keycloak</span> adapter run the following commands:
   ````
   For Linux:

     WILDFLY_HOME/bin/jboss-cli.sh -c --file=WILDFLY_HOME/bin/adapter-install.cli
     WILDFLY_HOME/bin/jboss-cli.sh -c --command=:reload

   For Windows:

    WILDFLY_HOME\bin\jboss-cli.bat -c --file=WILDFLY_HOME\bin\adapter-install.cli
    WILDFLY_HOME\bin\jboss-cli.bat -c --command=:reload
   ````
4. If you plan to try the SAML examples you also need to install <span>Keycloak</span> SAML adapter:

   ````
   For Linux:

     WILDFLY_HOME/bin/jboss-cli.sh -c --file=WILDFLY_HOME/bin/adapter-install-saml.cli
     WILDFLY_HOME/bin/jboss-cli.sh -c --command=:reload

   For Windows:

     WILDFLY_HOME\bin\jboss-cli.bat -c --file=WILDFLY_HOME\bin\adapter-install-saml.cli
     WILDFLY_HOME\bin\jboss-cli.bat -c --command=:reload
   ````

# Examples

* [app-authz-rest-employee](../app-authz-rest-employee/README.md) - SpringBoot REST Service Protected Using Keycloak Authorization Services
* [app-jee-html5](../app-jee-html5/README.md) - HTML5 application that invokes the example service. Requires service example to be deployed.
* [app-jee-jsp](../app-jee-jsp/README.md) - JSP application packaged that invokes the example service. Requires service example to be deployed.
* [app-profile-jee-html5](../app-profile-jee-html5/README.md) - HTML5 application that displays user profile and token details.
* [app-profile-jee-jsp](../app-profile-jee-jsp/README.md) - JSP application that displays user profile and token details.
* [app-profile-jee-vanilla](../app-profile-jee-vanilla/README.md) - JSP application configured with basic authentication. Shows how to secure an application with the client adapter subsystem.
* [app-profile-saml-jee-jsp](../app-profile-saml-jee-jsp/README.md) - JSP application that uses SAML and displays user profile.
* [app-springboot](../app-springboot/README.md) - SpringBoot application that is secured with Keycloak. [Requires SpringBoot Service](../service-springboot-rest/README.md).
* [service-jee-jaxrs](../service-jee-jaxrs/README.md) - JAX-RS Service with public and protected endpoints.


# Troubleshooting

| Problem | Probable Cause | Possible Solution |
|---------|----------------|-------------------|
| Some required files are missing / Some Enforcer rules have failed | Client adapter config is missing | Add client adapter installation file to `config` directory as specified in quickstart README.md |
| Unknown authentication mechanism KEYCLOAK | OpenID Connect client adapter missing | Install OpenID Connect adapter as specified in the [Start and Configure the WildFly Server](#wildfly) section |
| Unknown authentication mechanism KEYCLOAK-SAML | SAML client adapter missing | Install SAML adapter as specified in the [Start and Configure the WildFly Server](#wildfly) section |
| Failed to invoke service: 404 Not Found | Service not deployed, or service URL not correct | Deploy service or change the URL for the service as specified in the quickstart README
| Failed to invoke service: Request failed message with no error code | CORS not enabled | Most likely cause is that you've deployed the HTML5 application to a different host than the service, if so the solution is to add CORS support to the service. See the README for the service for how to enable. |
| Page displays: Forbidden | Authenticated user is missing a role required to access the url | This can happen if you fail to add `user` role to admin user as instructed in [Create Roles and User](#add-roles-user). |
