app-authz-spring-security: Spring Security Application using Authorization Services
===================================================

Level: Beginner
Technologies: SpringBoot, Spring MVC, Spring Security
Summary: SpringBoot, Spring MVC, Spring Security
Target Product: <span>Keycloak</span>
Source: <https://github.com/keycloak/keycloak-quickstarts>


What is it?
-----------

The `app-authz-spring-security` quickstart demonstrates how to write a Spring Security application where both authentication and
authorization aspects are managed by <span>Keycloak</span>.

This application tries to focus on the authorization features provided by <span>Keycloak</span> Authorization Services, where resources are
protected by a set of permissions and policies defined in Keycloak itself and access to these resources are enforced by a policy enforcer
that intercepts every single request to the application.

In this application, there are three paths protected by specific permissions in <span>Keycloak</span>:

* **/protected**, where access to this page is based on the evaluation of permissions associated with a resource **Protected Resource** in <span>Keycloak</span>. Basically,
any user with a role *user* is allowed to access this page.

* **/protected/premium**, where access to this page is based on the evaluation of permissions associated with a resource **Premium Resource** in <span>Keycloak</span>. Basically,
only users with a role *user-premium* is allowed to access this page.

* **/protected/alice**, where access to this page is based on the evaluation of permissions associated with a resource **Alice Resource** in <span>Keycloak</span>. Basically,
only user *alice* is allowed to access this page.

The home page (home.ftl) also demonstrates how to use a ``AuthorizationContext`` instance to check for user's permissions and hide/show
things in a page. Where the ``AuthorizationContext`` encapsulates all permissions granted by a <span>Keycloak</span> server and provides methods
to check these permissions.

You can use two distinct users to access this application:

|Username|Password|Roles|
|---|---|---|
|alice|alice|user|
|jdoe|jdoe|user, user-premium|


System Requirements
-------------------

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later.


Configuration in <span>Keycloak</span>
-----------------------

Prior to running the quickstart you need to create a `realm` in <span>Keycloak</span> with all the necessary configuration to deploy and run the quickstart.

To create the realm required for this quickstart, follow these steps:

1. Open the <span>Keycloak</span> admin console
2. In the top left corner dropdown menu that is titled `Master`, click `Add Realm`. If you are logged in to the master realm this dropdown menu lists all the realms created.
3. For this quickstart we are not going to manually create the realm, but import all configuration from a JSON file. Click on `Select File` and import the [config/quickstart-realm.json](config/realm-import.json).
4. Click `Create`

The steps above will result on a new `spring-security-quickstart` realm.

Build and Run the Quickstart
-------------------------------

Make sure your <span>Keycloak</span> server is running on <http://localhost:8180/>. For that, you can start the server using the command below:

   ````
   cd {KEYCLOAK_HOME}/bin
   ./standalone.sh -Djboss.socket.binding.port-offset=100
   
   ````

If your server is up and running, perform the following steps to start the application:

1. Open a terminal and navigate to the root directory of this quickstart.

2. The following shows the command to deploy the quickstart:

   ````
   mvn spring-boot:run

   ````

Access the Quickstart
---------------------

You can access the application with the following URL: <http://localhost:8080/>.

Integration test of the Quickstart
----------------------------------

1. Make sure you have an <span>Keycloak</span> server running with an admin user in the `master` realm or use the provided docker image
2. Be sure to set the `TestHelper.keycloakBaseUrl` in the `createArchive` method (default URL is localhost:8180/auth).
3. Set accordingly the correct url for the `keycloak.auth-server-url` in the test [application.properties](src/test/resources/application.properties).
4. Run `mvn test -Pspring-boot`