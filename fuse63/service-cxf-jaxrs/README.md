service-cxf-jaxrs: Apache CXF JAX-RS Service
============================================

Level: Beginner
Technologies: Apache CXF, JavaEE, JBoss Fuse
Summary: JAX-RS Service
Target Product: Keycloak, JBoss Fuse
Source: <https://github.com/redhat-developer/redhat-sso-quickstarts>


What is it?
-----------

The `service-cxf-jaxrs` quickstart demonstrates how to write a Apache CXF JAX-RS service that is secured with Keycloak and deploy
it to JBoss Fuse.

There are 3 endpoints exposed by the service:

* `public` - requires no authentication
* `secured` - can be invoked by users with the `user` role
* `admin` - can be invoked by users with the `admin` role

The endpoints are very simple and will only return a simple message stating what endpoint was invoked.


System Requirements
-------------------

You need to have JBoss Fuse 6.3.0

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later.


Configuration in Keycloak
-----------------------

Prior to running the quickstart you need to create a client in Keycloak and download the installation file.

The following steps shows how to create the client required for this quickstart:

* Open the Keycloak admin console
* Select `Clients` from the menu
* Click `Create`
* Add the following values:
  * Client ID: You choose (for example `fuse-service-cxf`)
  * Client Protocol: `openid-connect`
* Click `Save`

Once saved you need to change the `Access Type` to `bearer-only` and click save.

Finally you need to configure the adapter, this is done by retrieving the adapter configuration file:

* Click on `Installation` in the tab for the client you created
* Select `<span>Keycloak</span> OIDC JSON`
* Click `Download`
* Move the file `keycloak.json` to the directory `src/main/resources/config/`in the quickstart

You may also want to enable CORS for the service if you want to allow invocations from HTML5 applications deployed to a
different host. To do this edit `keycloak.json` and add:

```
{
   ...
   "enable-cors": true
}
```

As an alternative you can create the client by importing the file [client-import.json](config/client-import.json) and
copying [config/keycloak-example.json](config/keycloak-example.json) to `src/main/resources/config/keycloak.json`.

Build the Quickstart
--------------------

1. Open a terminal and navigate to the [fuse63](../fuse63) directory.

```
mvn clean install
```


Access the Quickstart
---------------------

The endpoints for the service are:

* public - <http://localhost:8282/service/public>
* secured - <http://localhost:8282/service/secured>
* admin - <http://localhost:8282/service/admin>

You can open the public endpoint directly in the browser to test the service. The two other endpoints require
invoking with a bearer token. To invoke these endpoints use the example quickstart:

* [app-war-jsp](../app-war/README.md) - JSP application packaged that invokes the example service. Requires service example to be deployed.
