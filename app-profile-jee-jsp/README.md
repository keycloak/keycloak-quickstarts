app-profile-jee-jsp: JSP Profile Application
================================================

Level: Beginner  
Technologies: JavaEE  
Summary: JSP Profile Application  
Target Product: RH-SSO, JBoss EAP  
Source: <https://github.com/keycloak/rh-sso-quickstarts>


What is it?
-----------

The `app-profile-jee-jsp` quickstart demonstrates how to write an application with JavaEE that
authenticates using RH-SSO. Once authenticated the application shows the users profile information and can also
display the token retrieved from RH-SSO.


System Requirements
------------

If you are deploying the application as a WAR you need to have JBoss EAP 6.4 or 7 running.

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later.


Configuration in RH-SSO
-----------------------

Prior to running the quickstart you need to create a client in RH-SSO and download the installation file.

The following steps shows how to create the client required for this quickstart:

* Open the RH-SSO admin console
* Select `Clients` from the menu
* Click `Create`
* Add the following values:
  * Client ID: You choose (for example `app-profile-jsp`)
  * Client Protocol: `openid-connect`
  * Root URL: URL to the application (for example `http://localhost:8080/app-profile-jsp`)
* Click `Save`

Once saved you need to change the Access Type to `confidential` and click `Save`.

If you deploy the application somewhere else change the hostname and port of the URLs accordingly.

Finally you need to configure the adapter, this is done by retrieving the adapter configuration file:

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
   For JBoss EAP 6.4: mvn install -Deap6 jboss-as:deploy
   ````


Access the Quickstart
----------------------

You can access the application with the following URL: <http://localhost:8080/app-profile-jsp>


Undeploy the Quickstart
--------------------

1. Open a terminal and navigate to the root of the RH-SSO server directory.

2. The following shows the command to undeploy the quickstart:

   ````
   For JBoss EAP 7:   mvn install wildfly:undeploy
   For JBoss EAP 6.4: mvn install jboss-as:undeploy
   ````
