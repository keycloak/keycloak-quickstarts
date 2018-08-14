service-camel: Apache Camel Service
===================================

Level: Beginner
Technologies: Apache Camel, JBoss Fuse 7
Summary: Apache Camel Service
Target Product: Keycloak, JBoss Fuse 7
Source: <https://github.com/redhat-developer/redhat-sso-quickstarts>


What is it?
-----------

The `service-cxf-camel` quickstart demonstrates how to write a Apache Camel RestDSL service secured by Keycloak and deploy it to JBoss Fuse 7.

The service endpoint is very simple and will only return a simple message stating what endpoint was invoked. It doesn't require authentication for REST requests,
however those 2 REST endpoint subcontexts are secured:

* `secured` - can be invoked by users with the `user` role
* `admin` - can be invoked by users with the `admin` role

The endpoints are very simple and will only return a simple message stating what endpoint was invoked.


System Requirements
-------------------

You need to have JBoss Fuse 7.0.1 or newer

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later.


Configuration in Keycloak
-----------------------

Prior to running the quickstart you need to create a client in Keycloak and download the installation file.

The following steps shows how to create the client required for this quickstart:

* Open the Keycloak admin console
* Select `Clients` from the menu
* Click `Create`
* Add the following values:
  * Client ID: You choose (for example `fuse-service-camel`)
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

1. Open a terminal and navigate to the [fuse70](..) directory.

```
mvn clean install
```

Access the Quickstart
---------------------

The endpoints for the service are anything beyond the <http://localhost:8383/service> . For example if you open your browser and access the URL
like <http://localhost:8383/service/foo>, you will receive simple echo-like response: `{"message": "camel - foo"}` .

All the endpoints are publicly accessible besides those 2 subcontexts:

* secured - <http://localhost:8383/service/secured>
* admin - <http://localhost:8383/service/admin>

Those endpoints require invoking with a bearer token. To invoke these endpoints use the example quickstart:

* [app-war-jsp](../app-war/README.md) - JSP application packaged that invokes the example service. Requires service example to be deployed.
