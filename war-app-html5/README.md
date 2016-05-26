# war-app-html5: 

Author: Stan Silvert  
Level: Beginner  
Technologies: Java, JavaScript, HTML5  
Summary: Simple HTML5 WAR-based application exposing three different REST endpoints.   
Target Product: RH-SSO  
Source: <https://github.com/keycloak/rh-sso-quickstarts>  

What is it?
-----------

Simple HTML5 WAR-based application exposing three different REST endpoints.

Settings
--------

The example assumes you have the example services running. If the services are not hosted on ``http://localhost:8080/service`` you need to edit ``app.js`` and replace the value of ``serviceUrl``.

You need to create a client in RH-SSO and download the installation file. The configuration options when creating the client should be:

* Client ID: You choose
* Access Type: public
* Root URL: Root URL for where you're hosting the application (for example http://localhost)
* Valid Redirect URIs: ``/*``
* Web Origins: ``+``

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

You can run example with the following URL: <http://localhost:8080/jboss-war-app-html5>

Undeploy the Archive
--------------------

* Make sure you have started the JBoss EAP server as described above.
* Open a command prompt and navigate to the root directory of this quickstart.

mvn wildfly:undeploy
