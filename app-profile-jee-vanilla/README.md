# app-profile-jee: 

Author: Bill Burke  
Level: Beginner  
Technologies: Java, Servlets  
Summary: Simple Java Servlet application allowing login.   
Target Product: RH-SSO  
Source: <https://github.com/keycloak/rh-sso-quickstarts>  

What is it?
-----------

This is a simple Java Servlet application.  It is used by the Getting Started Tutorials, Securing a JBoss Servlet Application chapter.

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

You can run example with the following URL: <http://localhost:8080/vanilla>

Undeploy the Archive
--------------------

* Make sure you have started the JBoss EAP server as described above.
* Open a command prompt and navigate to the root directory of this quickstart.

mvn wildfly:undeploy
