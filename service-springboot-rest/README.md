service-springboot-rest: SpringBoot REST Service
===================================================

Level: Beginner
Technologies: SpringBoot
Summary: SpringBoot REST Service
Target Product: Keycloak
Source: <https://github.com/keycloak/Keycloak-quickstarts>


What is it?
-----------

The `service-springboot-rest` quickstart demonstrates how to write a RESTful service with SpringBoot that is secured with Keycloak.

There are 2 endpoints exposed by the service:

* `public` - requires no authentication
* `products` - can be invoked by users with the `user` role and returns a list of products





System Requirements
-------------------

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later.


Configuration in Keycloak
-----------------------

Prior to running the quickstart you need to create a client in Keycloak and download the installation file.

The following steps shows how to create the client required for this quickstart:

* Open the Keycloak admin console
* Select `Clients` from the menu
* Click `Create`
* Add the following values:
  * Client ID: You choose (for example `service-springboot`)
  * Client Protocol: `openid-connect`
* Click `Save`

Once saved you need to change the `Access Type` to `bearer-only` and click save.

Finally you need to update the adapter using SpringBoot's configuration file (application.properties) :

````
server.compression.enabled: true
server.compression.min-response-size: 1
server.connection-timeout=5000
server.port = 8081
keycloak.realm=springboot-quickstart
keycloak.auth-server-url=http://localhost:8180/auth
keycloak.ssl-required=external
keycloak.resource=product-service
keycloak.public-client=true
keycloak.bearer-only=true
keycloak.securityConstraints[0].securityCollections[0].name = protected resource
keycloak.securityConstraints[0].securityCollections[0].authRoles[0] = ROLE_USER
keycloak.securityConstraints[0].securityCollections[0].patterns[0] = /products

````



Build and Run the Quickstart
-------------------------------

1. Open a terminal and navigate to the root directory of this quickstart.

2. The following shows the command to deploy the quickstart:

   ````
   mvn spring-boot:run

   ````

Access the Quickstart
---------------------

The endpoints for the service are:

* public - <http://localhost:8081/public>
* secured - <http://localhost:8080/products>


You can open the public endpoint directly in the browser to test the service. The other endpoint require
invoking with a bearer token. To invoke these endpoints use one of the example quickstarts:

* [app-springboot](../app-springboot/README.md) - SpringBoot application that invokes the example service. Requires service example to be deployed.
* [app-jee-html5](../app-jee-html5/README.md) - HTML5 application that invokes the example service. Requires service example to be deployed.
* [app-jee-jsp](../app-jee-jsp/README.md) - JSP application packaged that invokes the example service. Requires service example to be deployed.

Integration test of the Quickstart
----------------------------------

1. Make sure you have an Keycloak server running with an admin user in the `master` realm or use the provided docker image
2. Be sure to set the `TestHelper.keycloakBaseUrl` in the `createArchive` method (default URL is localhost:8180/auth).
3. Set accordingly the correct url for the `keycloak.auth-server-url` in the test [application.properties](src/test/resources/application.properties).
4. Run `mvn test`

