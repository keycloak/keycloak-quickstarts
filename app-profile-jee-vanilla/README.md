app-profile-jee-vanilla: JSP Profile Application
================================================

Level: Beginner  
Technologies: JavaEE  
Summary: JSP Profile Application with Basic Authentication  
Target Product: RH-SSO, JBoss EAP  
Source: <https://github.com/keycloak/rh-sso-quickstarts>  


What is it?
-----------

The `app-profile-jee-vanilla` quickstart demonstrates how to change a JavaEE application that is secured with basic
authentication without any changes to the WAR itself. Changing the authentication method and injecting the
configuration is done automatically by the RH-SSO client adapter subsystem.


System Requirements
-------------------

If you are deploying the application as a WAR you need to have JBoss EAP 6.4 or 7 running.

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later.


Build and Deploy the Quickstart
-------------------------------

Unlike most other quickstarts for this quickstart you should first deploy the application so you can see that
it's secured with basic authentication. Afterwards you will configure the client adapter subsystem to secure the
application and re-deploy the application to see the application is now secured with Keycloak without having to do
any changes to the application itself.

1. Open a terminal and navigate to the root directory of this quickstart.

2. The following shows the command to deploy the quickstart:

   ````
   For JBoss EAP 7:   mvn install wildfly:deploy
   For JBoss EAP 6.4: mvn install -Deap6 jboss-as:deploy
   ````


Access the Quickstart
----------------------

You can access the application with the following URL: <http://localhost:8080/vanilla>. If you click on the
login button the browser will display a prompt for authentication required. This is used for basic authentication where
a username and password is collected by the browser and sent to the web application with the authorization header.

At the moment you are not able to authenticate unless you have configured your JBoss EAP server with a realm and users
for basic authentication.

The next step is to configure the RH-SSO client adapter subsystem to configure the application to use RH-SSO for
authentication instead.


Configure Client Adapter Subsystem
----------------------------------

Before configuring the adapter subsystem you need to create a client in RH-SSO.

The following steps show how to create the client required for this quickstart:

* Open the RH-SSO admin console
* Select `Clients` from the menu
* Click `Create`
* Add the following values:
  * Client ID: You choose (for example `app-profile-vanilla`)
  * Client Protocol: `openid-connect`
  * Root URL: URL to the application (for example `http://localhost:8080/vanilla`)
* Click `Save`

Once saved you need to change the Access Type to `confidential` and click `Save`.

If you deploy the application somewhere else change the hostname and port of the URLs accordingly.

As an alternative you can create the client by importing the file [client-import.json](config/client-import.json).

Next, configure the OIDC adapter via the RH-SSO client adapter subsystem. To do this use the following steps:

* Click on `Installation` in the tab for the client you created
* Select `Keycloak OIDC JBoss Subsystem XML`
* Copy the XML snippet to the clipboard
* Open `EAP_HOME/standalone/configuration/standalone.xml` in an editor
* Locate the element `<subsystem xmlns="urn:jboss:domain:keycloak:1.1"/>` and add the above snippet as a child element. For example:

  ````
  <subsystem xmlns="urn:jboss:domain:keycloak:...">
      ...
      <secure-deployment name="WAR MODULE NAME.war">
          <realm>master</realm>
          <realm-public-key>MIIBIj...</realm-public-key>
          <auth-server-url>http://localhost:8180/auth</auth-server-url>
          <ssl-required>EXTERNAL</ssl-required>
          <resource>app-profile-vanilla</resource>
          <credential name="secret">57826...</credential>
      </secure-deployment>
  </subsystem>
  ````
  
* Replace `WAR MODULE NAME.war` with `vanilla.war`

Now restart the JBoss EAP server. After the server is restarted open <http://localhost:8080/vanilla> and try
to login again. This time you will be redirected to RH-SSO to authenticate.


Undeploy the Quickstart
--------------------

1. Open a terminal and navigate to the root of the RH-SSO server directory.

2. The following shows the command to undeploy the quickstart:

   ````
   For JBoss EAP 7:   mvn wildfly:undeploy
   For JBoss EAP 6.4: mvn jboss-as:undeploy
   ````
