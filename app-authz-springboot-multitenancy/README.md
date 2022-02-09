app-authz-springboot: SpringBoot Web Multi-Tenant Application using Authorization Services
===================================================

Level: Beginner
Technologies: SpringBoot, Spring MVC, Spring Security
Summary: SpringBoot, Spring MVC, Spring Security
Target Product: <span>Keycloak</span>
Source: <https://github.com/keycloak/Keycloak-quickstarts>


What is it?
-----------

The `app-authz-springboot-multitenancy` quickstart demonstrates how to write a SpringBoot Web application where both authentication and
authorization aspects are managed by <span>Keycloak</span>.

This application tries to focus on the multitenancy capabilities provided by the Keycloak Spring Boot Adapter so that you
can support multiple tenants from a single application.

As tenants, this application uses two distinct realms. For each realm there is a client and a user where depending on the requested URL the proper tenant is chosen and the user is redirected to Keycloak to authenticate.

In this application, there are three paths protected by specific permissions in <span>Keycloak</span>:

* **/protected**, where access to this page is based on the evaluation of permissions associated with a resource **Protected Resource** in <span>Keycloak</span>. Basically,
any user with a role *user* is allowed to access this page.

* **/protected/premium**, where access to this page is based on the evaluation of permissions associated with a resource **Premium Resource** in <span>Keycloak</span>. Basically,
only users with a role *user-premium* is allowed to access this page.

The home page (home.ftl) also demonstrates how to use a ``AuthorizationContext`` instance to check for user`s permissions and hide/show
things in a page. Where the ``AuthorizationContext`` encapsulates all permissions granted by a <span>Keycloak</span> server and provides methods
to check these permissions.

You can use these users to access this application:

|Realm|Username|Password|Roles|
|---|---|---|---|
|realm-a|alice|alice|user|
|realm-b|jdoe|jdoe|user|
|realm-b|kolo|kolo|user, premium|

Users from tenant `realm-a` can access any resource. The authorization settings are very permissive so that any user can access any of the resources served by the application.

However, for tenant `tealm-b`, only the user `kolo` is supposed to access `premium` resources from the URL `/realm-b/protected/premium`.

Based on these initial settings, you should be able to not only support authenticating to different tenants but also having specific authorization settings for the resources protected by each the tenant.

System Requirements
-------------------

See the [Getting Started Guide](../docs/getting-started.md) for the minimum requirements and steps to build and run the quickstart.


Configuration in <span>Keycloak</span>
-----------------------

Prior to running the quickstart you need to create a `realm` in <span>Keycloak</span> with all the necessary configuration to deploy and run the quickstart.

The following steps show how to create the realms required for this quickstart:

* Open the <span>Keycloak</span> admin console
* In the top left corner dropdown menu that is titled `Master`, click `Add Realm`. If you are logged in to the master realm this dropdown menu lists all the realms created.
* For this quickstart we are not going to manually create the realm, but import all configuration from a JSON file. Click on `Select File` and import the [config/realm-a-realm.json](config/realm-a-realm.json).
* Follow the same steps as above to import a second realm using the [config/realm-b-realm.json](config/realm-b-realm.json) file.
* Click `Create`

The steps above will result on two new realms: `realm-a` and `realm-b`.

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

You can access the application with the following URL: <http://localhost:8080/realm-a>.

To access tenant `realm-b` you can type the following URL in your browser: <http://localhost:8080/realm-b>. Or select the tenant from a combobox at the home page.

Integration test of the Quickstart
----------------------------------

1. Make sure you have an <span>Keycloak</span> server running with an admin user in the `master` realm or use the provided docker image
2. Be sure to set the `TestHelper.keycloakBaseUrl` in the `createArchive` method (default URL is localhost:8180/auth).
3. Set accordingly the correct url for the `keycloak.auth-server-url` in the test [application.properties](src/test/resources/application.properties).
4. Run `mvn test -Pspring-boot`