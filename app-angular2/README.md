app-angular2: Angular 2 Service Invocation Application
===================================================

Level: Beginner  
Technologies: Angular2, JavaScript  
Summary: Angular2 Service Invocation Application packaged as a WAR  
Target Product: <span>Keycloak</span>, <span>WildFly</span>  
Source: <https://github.com/keycloak/keycloak-quickstarts>

What is it?
-----------

The `app-angular2` quickstart demonstrates how to write an application with Angular2 and JavaScript that authenticates
using <span>Keycloak</span>. Once authenticated the application shows how to invoke a service secured with <span>Keycloak</span>.

For simplicity of deploying the application it is packaged as a WAR archive and can be deployed to <span>WildFly</span>.
As the example only contains static html pages the files in `src/main/webapp` can also be hosted on any web server.


System Requirements
-------------------

The quickstart requires that you have the [example services](../service-jee-jaxrs/README.md) running. It assumes the
services are located at `http://localhost:8080/service`. If the services are running elsewhere you need to edit
`app.component.ts` and replace the value of `serviceUrl`.  Then transpile using `npm run build` from the webapp
directory.

If you are deploying the application as a WAR you need to have <span>WildFly 10</span> running.

To build this project, you will need nodejs/npm.  To make modifications you will
need to install TypeScript.  For nodejs/npm, go to `https://nodejs.org`.  Once installed,
the the following command will install TypeScript:
````
npm install -g typescript
````
To build and run this project as a WAR you will need Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later.


Configuration in <span>Keycloak</span>
-----------------------

Prior to running the quickstart you need to create a client in <span>Keycloak</span> and download the installation file.

The following steps show how to create the client required for this quickstart:

* Open the <span>Keycloak</span> admin console
* Select `Clients` from the menu
* Click `Create`
* Add the following values:
  * Client ID: You choose (for example `app-angular2`)
  * Client Protocol: `openid-connect`
  * Root URL: URL to the application (for example `http://localhost:8080/app-angular2`).
* Click `Save`

If you deploy the application somewhere else change the hostname and port of the URLs accordingly.

Once saved you need to change the `Access Type` to `public` and click save.

Finally you need to configure the javascript adapter, this is done by retrieving the adapter configuration file:

* Click on `Installation` in the tab for the client you created
* Select `Keycloak OIDC JSON`
* Click `Download`
* Move the file `keycloak.json` to the `config/` directory in the root of the quickstart

As an alternative you can create the client by importing the file [client-import.json](config/client-import.json) and
copying [config/keycloak-example.json](config/keycloak-example.json) to `config/keycloak.json`.


Build and Deploy the Quickstart
--------------------------------

1. Open a terminal and navigate to the root directory of this quickstart.

2. Change directory to ``src\main\webapp``

3. Run ``npm install``

4. The following shows the command to deploy the quickstart:

   ````
   mvn clean wildfly:deploy

   ````
5. Any time you wish to rebuild the TypeScript-generated javascript files, run
``npm run build`` or to constantly watch for changes, run ``npm run build:watch``.

Access the Quickstart
---------------------

You can access the application with the following URL: <http://localhost:8080/app-angular2>.

The application provides buttons that allows invoking the different endpoints on the service:

* Invoke public - Invokes the public endpoint and doesn't require a user to be logged-in
* Invoke secured - Invokes the secured endpoint and requires a user with the role `user` to be logged-in
* Invoke admin - Invokes the secured endpoint and requires a user with the role `admin` to be logged-in

If you invoke the endpoints without the required permissions an error will be shown.


Undeploy the Quickstart
-----------------------

1. Open a terminal and navigate to the root directory of this quickstart.

2. The following shows the command to undeploy the quickstart:

   ````
   mvn wildfly:undeploy

   ````
