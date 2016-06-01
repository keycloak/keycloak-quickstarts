service-jee-jaxrs: JAX-RS Service
===================================================

Level: Beginner  
Technologies: JavaEE  
Summary: JAX-RS Service  
Target Product: RH-SSO, JBoss EAP  
Source: <https://github.com/keycloak/rh-sso-quickstarts>


What is it?
-----------

The `service-jee-jaxrs` quickstart demonstrates how to write a RESTful service with JAX-RS that is secured with RH-SSO.

There are 3 endpoints exposed by the service:

* `public` - requires no authentication
* `secured` - can be invoked by users with the `user` role
* `admin` - can be invoked by users with the `admin` role

The endpoints are very simple and will only return a simple message stating what endpoint was invoked.


System Requirements
-------------------

You need to have JBoss EAP 6.4 or 7 running.

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later.


Configuration in RH-SSO
-----------------------

Prior to running the quickstart you need to create a client in RH-SSO and download the installation file.

The following steps shows how to create the client required for this quickstart:

* Open the RH-SSO admin console
* Select `Clients` from the menu
* Click `Create`
* Add the following values:
  * Client ID: You choose (for example `service-jaxrs`)
  * Client Protocol: `openid-connect`
* Click `Save`

Once saved you need to change the `Access Type` to `bearer-only` and click save.

Finally you need to configure the adapter, this is done by retrieving the adapter configuration file:

* Click on `Installation` in the tab for the client you created
* Select `Keycloak OIDC JSON`
* Click `Download`
* Move the file `keycloak.json` to the `config/` directory in the root of the quickstart

You may also want to enable CORS for the service if you want to allow invocations from HTML5 applications deployed to a
different host. To do this edit `keycloak.json` and add:

````
{
   ...
   "enable-cors": true
}
````


Build and Deploy the Quickstart
-------------------------------

1. Open a terminal and navigate to the root directory of this quickstart.

2. The following shows the command to deploy the quickstart:

   ````
   For JBoss EAP 7:   mvn install wildfly:deploy
   For JBoss EAP 6.4: mvn install -Deap6 jboss-as:deploy
   ````


Access the Quickstart
---------------------

The endpoints for the service are:

* public - <http://localhost:8080/service/public>
* secured - <http://localhost:8080/service/secured>
* admin - <http://localhost:8080/service/admin>

You can open the public endpoint directly in the browser to test the service. The two other endpoints require
invoking with a bearer token. To invoke these endpoints use one of the example quickstarts:

* [app-jee-html5](../app-jee-html5/README.md) - HTML5 application that invokes the example service. Requires service example to be deployed.
* [app-jee-jsp](../app-jee-jsp/README.md) - JSP application packaged that invokes the example service. Requires service example to be deployed.


Undeploy the Quickstart
-----------------------

1. Open a terminal and navigate to the root directory of this quickstart.

2. The following shows the command to undeploy the quickstart:

   ````
   For JBoss EAP 7:   mvn install wildfly:undeploy
   For JBoss EAP 6.4: mvn install jboss-as:undeploy
   ````
