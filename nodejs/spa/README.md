app-nodejs-html5: HTML5 Service Invocation Application
===================================================

Level: Beginner
Technologies: HTML5, JavaScript
Summary: HTML5 Service Invocation Application run under Express
Target Product: Keycloak
Source: <https://github.com/keycloak/keycloak-quickstarts>

What is it?
-----------

The `app-nodejs-html5` quickstart demonstrates how to write an application with HTML5 and JavaScript that authenticates
using Keycloak. Once authenticated the application shows how to invoke a service secured with Keycloak.

System Requirements
-------------------

You need to have Node.js version 12.x or later installed.

The quickstart requires that you have the [service-nodejs](../service-nodejs/README.md) running. It assumes the
services are located at `http://localhost:3000/service`. If the services are running elsewhere you need to edit
`app.js` and replace the value of `serviceUrl`.

Configuration in Keycloak
-----------------------

Prior to running the quickstart you need to create a client in Keycloak and download the installation file.

The following steps show how to create the client required for this quickstart:

* Open the Keycloak admin console
* Select `Clients` from the menu
* Click `Create`
* Add the following values:
  * Client ID: You choose (for example `app-html5`)
  * Client Protocol: `openid-connect`
  * Root URL: URL to the application (for example `http://localhost:8080/app-html5`).
* Click `Save`

If you deploy the application somewhere else change the hostname and port of the URLs accordingly.

Once saved you need to change the `Access Type` to `public` and click save.

Finally you need to configure the JavaScript adapter, this is done by retrieving the adapter configuration file:

* Click on `Installation` in the tab for the client you created
* Select `Keycloak OIDC JSON`
* Click `Download`
* Move the file `keycloak.json` to the `src/main/webapp` directory in the root of the quickstart

As an alternative you can create the client by importing the file [client-import.json](config/client-import.json) and
copying [config/keycloak-example.json](config/keycloak-example.json) to `src/main/webapp/keycloak.json`.


Build and Deploy the Quickstart
--------------------------------

1. Open a terminal and navigate to the root directory of this quickstart.

2. The following shows the command to deploy the quickstart:

   ````
   npm install
   npm run start
   ````

Access the Quickstart
---------------------

You can access the application with the following URL: <http://localhost:8080/app-html5>.

The application provides buttons that allows invoking the different endpoints on the service:

* Invoke public - Invokes the public endpoint and doesn't require a user to be logged-in
* Invoke secured - Invokes the secured endpoint and requires a user with the role `user` to be logged-in
* Invoke admin - Invokes the secured endpoint and requires a user with the role `admin` to be logged-in

If you invoke the endpoints without the required permissions an error will be shown.

