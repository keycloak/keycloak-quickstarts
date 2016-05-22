# app-profile-jee-saml: 

Author: Stan Silvert  
Level: Beginner  
Technologies: Java, JSP, SAML  
Summary: Simple Java EE application allowing login/logout and displaying full name of the user obtained from the token.   
Target Product: RH-SSO  
Source: <https://github.com/keycloak/rh-sso-quickstarts>  

What is it?
-----------

Simple Java EE application allowing login/logout and displaying full name of the user obtained from the token.


Settings
-----------

You need to create a client in RH-SSO. The configuration options when creating the client should be:
* Client ID: app-profile-jee-saml
* Enabled: ON
* Consent Required: OFF
* direct-grants-only: OFF
* Client Protocol: saml
* Include AuthnStatement: ON
* Sign Documents: OFF
* Sign Assertions: OFF
* Encrypt Assertions: OFF
* Client Signature Required: OFF
* Force POST Binding: OFF
* Front Channel Logout: OFF
* Force Name ID Format: OFF
* Name ID Format: username
* Root URL: <blank>
* Valid Redirect URIs: http://localhost:8080/app-profile-jee-saml/*
* Base URL: http://localhost:8080/app-profile-jee-saml/
* Master SAML Processing URL: http://localhost:8080/app-profile-jee-saml/saml

Mappers
------------
Add all builtin mappers

Then, build the WAR with Maven and install as per the Adapter configuration for your server as described in the RH-SSO documentation.

System requirements
-------------------

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later. See Configure Maven for JBoss EAP 7 to make sure you are configured correctly for testing the quickstarts.

Start the JBoss EAP Server
--------------------------

1. Open a command prompt and navigate to the root of the JBoss EAP directory.
2. The following shows the command line to start the server:

For Linux:   EAP7_HOME/bin/standalone.sh
For Windows: EAP7_HOME\bin\standalone.bat

Build and Deploy the Quickstarts
--------------------------------

1. Make sure you have started the JBoss EAP server as described above.
2. Open a command prompt and navigate to the root directory of this quickstart.
3. Type this command to build and deploy the archive:

mvn clean install wildfly:deploy

4. This will deploy target/jboss-QUICKSTART_NAME.war to the running instance of the server.

Access the application
----------------------

You can run example with the following URL: <http://localhost:8080/app-profile-jee-saml/>

Undeploy the Archive
--------------------

* Make sure you have started the JBoss EAP server as described above.
* Open a command prompt and navigate to the root directory of this quickstart.

mvn wildfly:undeploy
