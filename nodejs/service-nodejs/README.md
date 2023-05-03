service-nodejs: Node.js Service
===================================================

Level: Beginner  
Technologies: Node.js  
Summary: Node.js Service  
Target Product: <span>Keycloak</span>, <span>WildFly</span>  
Source: <https://github.com/keycloak/keycloak-quickstarts>


What is it?
-----------

The `service-nodejs` quickstart demonstrates how to write a RESTful service with Node.js that is secured with <span>Keycloak</span>.

There are 3 endpoints exposed by the service:

* `public` - requires no authentication
* `secured` - can be invoked by users with the `user` role
* `admin` - can be invoked by users with the `admin` role

The endpoints are very simple and will only return a simple message stating what endpoint was invoked.


System Requirements
-------------------

All you need to build this project is Node.js 4.0.0 or later.

See the [Getting Started Guide](../docs/getting-started.md) for the minimum requirements and steps to build and run the quickstart.

Configuration in <span>Keycloak</span>
-----------------------

Prior to running the quickstart you need to import the *quickstart* realm. Clicking on the below link will bring you to the create realm page in the Admin UI.

[http://localhost:8180/auth/admin/master/console/#/create/realm](http://localhost:8180/auth/admin/master/console/#/create/realm)

Import the quickstart-realm.json file that is in the *keycloak-quickstarts/* directory.

Next thing you have to do is create a client in <span>Keycloak</span> and download the installation file.

The following steps shows how to create the client required for this quickstart:

* Open the <span>Keycloak</span> admin console
* Select `Clients` from the menu
* Click `Create`
* Add the following values:
  * Client ID: You choose (for example `service-nodejs`)
  * Client Protocol: `openid-connect`
* Click `Save`

Once saved you need to change the `Access Type` to `bearer-only` and click save.

Finally you need to configure the adapter, this is done by retrieving the adapter configuration file:

* Click on `Installation` in the tab for the client you created
* Select `Keycloak OIDC JSON`
* Click `Download`
* Move the file `keycloak.json` to the root folder of the quickstart

Because Java and Node.js apps are often deployed in separated environments, CORS is enabled by default for this service, it allows invocations from HTML5 applications deployed to a different host.

Build and Deploy the Quickstart
-------------------------------

1. Open a terminal and navigate to the root directory of this quickstart.

2. The following shows the command to run the quickstart:

   ````
   npm install
   node app
   ````

Access the Quickstart
---------------------

The endpoints for the service are:

* public - <http://localhost:3000/service/public>
* secured - <http://localhost:3000/service/secured>
* admin - <http://localhost:3000/service/admin>

You can open the public endpoint directly in the browser to test the service. The two other endpoints require
invoking with a bearer token. To invoke these endpoints use one of the example quickstarts:

* [app-jee-html5](../app-jee-html5/README.md) - HTML5 application that invokes the example service.
* [app-jee-jsp](../app-jee-jsp/README.md) - JSP application packaged that invokes the example service.

For both examples, please make sure that you properly changed the service URL to HTTP port 3000. For app-jee-html5 you can succeed that by changing the value of `serviceUrl` located on [app.js](../app-jee-html5/src/main/webapp/app.js) and for app-jee-jsp by setting the value of `SERVICE_URL` env variable or the `service.url` system property, pointing to http://localhost:3000 while deployment.