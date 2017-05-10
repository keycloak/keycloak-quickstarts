# Keycloak Quickstarts

The quickstarts demonstrate securing applications with Keycloak. They provide small, specific, working examples
that can be used as a reference for your own project.


Introduction
------------

These quickstarts run on WildFly Application Server 10.

Prior to running the quickstarts you should read this entire document and have completed the following steps:

* [Start and configure the Keycloak Server](#keycloak)
* [Start and configure the WildFly Server](#wildfly)

Afterwards you should read the README file for the quickstart you would like to deploy. See [examples](#examples) for
a list of the available quickstarts.

If you run into any problems please refer to the [troubleshooting](#troubleshooting) section.


Use of KEYCLOAK_HOME and WILDFLY_HOME Variables
-----------------------------------------

The quickstart README files use the replaceable value KEYCLOAK_HOME to denote the path to the Keycloak installation and the
value WILDFLY_HOME to denote the path to the WildFly installation. When you encounter this value in a README file, be sure
to replace it with the actual path to your installations.


System Requirements
-------------------

The applications these projects produce are designed to be run onWildFly Application Server 10.

All you need to build these projects is Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later.


<a id="Keycloak"></a>Start the Keycloak Server
------------------------------------------

By default the Keycloak Server uses the same ports as the WildFly Server. To run the quickstarts you can either run the
 Keycloak Server on a separate host (machine, VM, Docker, etc..) or on different ports.

To start the Keycloak server on a separate host:

1. Open a terminal on the separate machine and navigate to the root of the Keycloak server directory.

2. The following shows the command to start the Keycloak server:

   ````
   For Linux:   KEYCLOAK_HOME/bin/standalone.sh -b 0.0.0.0
   For Windows: KEYCLOAK_HOME\bin\standalone.bat -b 0.0.0.0
   ````

3. The URL of the Keycloak server will be http://&lt;HOSTNAME&gt;:8080 (replace &lt;HOSTNAME&gt; with the hostname of the separate host).

To start the Keycloak server on different ports:

1. Open a terminal and navigate to the root of the Keycloak server directory.

2. The following shows the command to start the Keycloak server:

   ````
   For Linux:   KEYCLOAK_HOME/bin/standalone.sh -Djboss.socket.binding.port-offset=100
   For Windows: KEYCLOAK_HOME\bin\standalone.bat -Djboss.socket.binding.port-offset=100
   ````

3. The URL of the Keycloak server will be *http://localhost:8180*

### <a id="add-admin"></a>Add Admin User

Open the main page for the Keycloak server ([localhost:8180](http://localhost:8180) or http://&lt;HOSTNAME&gt;:8080). If
this is a new installation of Keycloak server you will be instructed to create an initial admin user. To continue with
the quickstarts you need to do this prior to continuing.

### <a id="add-roles-user"></a>Create Roles and User

To be able to use the examples you need to create some roles as well as at least one sample user. To do first this open
the Keycloak admin console ([localhost:8180/auth/admin](http://localhost:8180/auth/admin) or http://&lt;HOSTNAME&gt;:8080/auth/admin) and
login with the admin user you created in the [add admin user](#add-admin) section.

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
the file [config/partial-import.json](config/partial-import.json) into your realm.

One more step, if you want to access the examples with the admin user you need to add the `user` role to admin user:

* Select `Users` from the menu
* Click `View all users`
* Click `Edit` for admin user
* Click `Role Mappings`
* Select `user` under `Available Roles` and click `Add selected`




<a id="wildfly"></a>Start and Configure the WildFly Server
--------------------------------------------------------------

Before starting the WildFly Server start by extracting the Keycloak client adapter into it.

For WildFly 9 or 10 extract `keycloak-wildfly-adapter-dist-2.5.0.Final.zip` into WILDFLY_HOME and for WildFly 8 extract
`keycloak-wf8-adapter-dist-2.5.0.Final.zip` into WILDFLY_HOME.

If you plan to try the SAML examples you also need the SAML WildFly adapter. To do this for WildFly 9 or 10 extract
`keycloak-saml-wildfly-adapter-dist.zip` into WILDFLY_HOME and for WildFly 8 extract
`keycloak-saml-eap6-adapter-dist.zip` into WILDFLY_HOME.

The next step is to start WildFly server:

1. Open a terminal and navigate to the root of the WildFly server directory.
2. Use the following command to start the WildFly server:
   ````
   For Linux:   EAP_HOME/bin/standalone.sh
   For Windows: EAP_HOME\bin\standalone.bat
   ````
3. To install the Keycloak adapter run the following commands:
   ````
   For Linux:

     WILDFLY_HOME/bin/jboss-cli.sh -c --file=WILDFLY_HOME/bin/adapter-install.cli
     WILDFLY_HOME/bin/jboss-cli.sh -c --command=:reload

   For Windows:

    WILDFLY_HOME\bin\jboss-cli.bat -c --file=WILDFLY_HOME\bin\adapter-install.cli
    WILDFLY_HOME\bin\jboss-cli.bat -c --command=:reload
   ````
4. If you plan to try the SAML examples you also need to install Keycloak SAML adapter:

   ````
   For Linux:

     WILDFLY_HOME/bin/jboss-cli.sh -c --file=WILDFLY_HOME/bin/adapter-install-saml.cli
     WILDFLY_HOME/bin/jboss-cli.sh -c --command=:reload

   For Windows:

     WILDFLY_HOME\bin\jboss-cli.bat -c --file=WILDFLY_HOME\bin\adapter-install-saml.cli
     WILDFLY_HOME\bin\jboss-cli.bat -c --command=:reload
   ````

Integration tests
-----------------

By default, the integration tests for each quickstart, expect this initial admin user to have `admin` as username and `admin` as password. This is configurable in each `ArquillianTest` class.

```
static {
    try {
        importTestRealm("admin", "admin", "/quickstart-realm.json");
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

If you don't have access to admin's credentials, please import the `quickstart-realm.json` from `src/test/resources`.

After that run integration tests:
```
mvn clean install -Pwildfly-managed -Denforcer.skip=true
```



Examples
--------

* [app-angular2](app-angular2/README.md) - Angular 2 application that invokes the example service. Requires service example to be deployed.
* [app-jee-html5](app-jee-html5/README.md) - HTML5 application that invokes the example service. Requires service example to be deployed.
* [app-jee-jsp](app-jee-jsp/README.md) - JSP application packaged that invokes the example service. Requires service example to be deployed.
* [app-profile-jee-html5](app-profile-jee-html5/README.md) - HTML5 application that displays user profile and token details.
* [app-profile-jee-jsp](app-profile-jee-jsp/README.md) - JSP application that displays user profile and token details.
* [app-profile-jee-vanilla](app-profile-jee-vanilla/README.md) - JSP application configured with basic authentication. Shows how to secure an application with the client adapter subsystem.
* [app-profile-saml-jee-jsp](app-profile-saml-jee-jsp/README.md) - JSP application that uses SAML and displays user profile.
* [service-jee-jaxrs](service-jee-jaxrs/README.md) - JAX-RS Service with public and protected endpoints.


Troubleshooting
---------------

| Problem | Probable Cause | Possible Solution |
|---------|----------------|-------------------|
| Some required files are missing / Some Enforcer rules have failed | Client adapter config is missing | Add client adapter installation file to `config` directory as specified in quickstart README.md |
| Unknown authentication mechanism KEYCLOAK | OpenID Connect client adapter missing | Install OpenID Connect adapter as specified in the [Start and Configure the WildFLy Server](#wildfly) section |
| Unknown authentication mechanism KEYCLOAK-SAML | SAML client adapter missing | Install SAML adapter as specified in the [Start and Configure the WildFLy Server](#wildfly) section |
| Failed to invoke service: 404 Not Found | Service not deployed, or service URL not correct | Deploy service or change the URL for the service as specified in the quickstart README
| Failed to invoke service: Request failed message with no error code | CORS not enabled | Most likely cause is that you've deployed the HTML5 application to a different host than the service, if so the solution is to add CORS support to the service. See the README for the service for how to enable. |
| Page displays: Forbidden | Authenticated user is missing a role required to access the url | This can happen if you fail to add `user` role to admin user as instructed in [Create Roles and User](#add-roles-user). |
