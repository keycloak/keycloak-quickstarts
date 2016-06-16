app-profile-jee-html5: HTML5 Profile Application
================================================

Level: Beginner  
Technologies: HTML5, JavaScript  
Summary: HTML5 Profile Application packaged as a WAR  
Target Product: RH-SSO, JBoss EAP  
Source: <https://github.com/keycloak/rh-sso-quickstarts>


What is it?
-----------

The `app-profile-jee-html5` quickstart demonstrates how to write an application with HTML5 and JavaScript that
authenticates using RH-SSO. Once authenticated the application shows the user's profile information and can also
display the token retrieved from RH-SSO.

For simplicity of deploying the application it is packaged as a WAR archive and can be deployed to JBoss EAP.
As the example only contains static html pages the files in `src/main/webapp` can also be hosted on any web server.


System Requirements
-------------------

If you are deploying the application as a WAR you need to have JBoss EAP 6.4 or 7 running.

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later.


Configuration in RH-SSO
-----------------------

Prior to running the quickstart you need to create a client in RH-SSO and download the installation file.

The following steps show how to create the client required for this quickstart:

* Open the RH-SSO admin console
* Select `Clients` from the menu
* Click `Create`
* Add the following values:
  * Client ID: You choose (for example `app-profile-html5`)
  * Client Protocol: `openid-connect`
  * Root URL: URL to the application (for example `http://localhost:8080/app-profile-html5`)
* Click `Save`

Once saved you need to change the `Access Type` to `public` and click save.

If you deploy the application somewhere else change the hostname and port of the URLs accordingly.

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

2. The following shows the command to deploy the quickstart:

   ````
   For JBoss EAP 7:   mvn install wildfly:deploy
   For JBoss EAP 6.4: mvn install jboss-as:deploy
   ````


Access the Quickstart
---------------------

You can access the application with the following URL: <http://localhost:8080/app-profile-html5>.


Undeploy the Quickstart
-----------------------

1. Open a terminal and navigate to the root directory of this quickstart.

2. The following shows the command to undeploy the quickstart:

   ````
   For JBoss EAP 7:   mvn wildfly:undeploy
   For JBoss EAP 6.4: mvn jboss-as:undeploy
   ````
