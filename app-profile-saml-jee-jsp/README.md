app-profile-saml-jee-jsp: HTML5 Profile Application with SAML
=============================================================

Level: Beginner  
Technologies: JavaEE  
Summary: JSP Profile Application  
Target Product: RH-SSO, JBoss EAP  
Source: <https://github.com/keycloak/rh-sso-quickstarts>


What is it?
-----------

The `app-profile-saml-jee-jsp` quickstart demonstrates how to write an application with JavaEE that
authenticates using RH-SSO over the SAML protocol. Once authenticated the application shows the users profile information.


System Requirements
-------------------

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
  * Client ID: You choose (for example `app-profile-saml`)
  * Client Protocol: `saml`
* Click `Save`

Once saved you need to change the following values:

* Valid Redirect URIs: `http://localhost:8080/app-profile-saml/*`
* Base URL: `http://localhost:8080/app-profile-saml/`
* Master SAML Processing URL: `http://localhost:8080/app-profile-saml/saml`
* Force Name ID Format: `ON`

If you deploy the application somewhere else change the hostname and port of the URLs accordingly.

To be able to retrieve the profile details next click on `Mappers`. Then click on `Add Builtin` and select all the
mappers before clicking `Add selected`.

Finally you need to configure the adapter, this is done by retrieving the adapter configuration file:

* Click on `Installation` in the tab for the client you created
* Select `Keycloak SAML Adapter keycloak-saml.xml`
* Click `Download`
* Edit `keycloak-saml.xml` and replace `SPECIFY YOUR LOGOUT PAGE!` with `/index.jsp`
* Move the file `keycloak-saml.xml` to the `config/` directory in the root of the quickstart

As an alternative you can create the client by importing the file [client-import.json](config/client-import.json) and
copying [config/keycloak-saml-example.xml](config/keycloak-saml-example.xml) to `config/keycloak-saml.xml`. Finally,
you need to edit `config/keycloak-saml.xml` and replace `REPLACE WITH REALM CERTIFICATE` with the actual realm certificate.
You can retrieve the realm certificate from the admin console from `Keys` under `Realm Settings`.


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

You can access the application with the following URL: <http://localhost:8080/app-profile-saml>


Undeploy the Quickstart
--------------------

1. Open a terminal and navigate to the root directory of this quickstart.

2. The following shows the command to undeploy the quickstart:

````
For JBoss EAP 7:   mvn install wildfly:undeploy
For JBoss EAP 6.4: mvn install jboss-as:undeploy
````
