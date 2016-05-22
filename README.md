# RH-SSO Quickstarts

What is it?
-----------
TODO improve description

With the exception of app-html5 and app-profile-html5, all examples are deployed as a WAR.  These examples assume that a RH-SSO server is running on localhost:8180 and a EAP server is running on localhost:8080.

If you want to change the address of the RH-SSO server, edit the keycloak.json file in each example's WEB-INF directory.
If you want to change the address of the EAP server, edit each example's client detail in the UI of the RH-SSO server.

Requirements and settings
-------------------------

1. Download this RH-SSO Quickstarts repository.
2. Download and unzip the plain **RH-SSO** distribution.
3. Download and unzip EAP 6.4/EAP 7
4. Download the EAP 6/EAP 7 OIDC adapter for EAP 6/EAP 7. Unzip this adapter on top of EAP.
5. Download the EAP 6/EAP 7 SAML adapter for EAP 6/EAP 7. Unzip this adapter on top of EAP. It's OK to overwrite modules.
6. *cd &lt;RH-SSO Home&gt;/bin*
7. *standalone(.bat or .sh) -Djboss.socket.binding.port-offset=100*
8. In your browser, go to <http://localhost:8180/auth/> and setup initial admin user (in later steps, we assume username "admin").
9. Go to <http://localhost:8180/auth/admin/index.html> and login as admin.
10. In the upper lefthand corner, click on Master->Add Realm
11. Click "Select File" and choose *examples-realm.json* from the root of the rh-sso-quickstarts repo from step 1.
12. *cd &lt;EAP Home&gt;/bin*
13. *standalone(.bat or .sh)*
14. With EAP running, open a new command prompt and *cd &lt;EAP Home&gt;/bin* again.  Then execute the following three commands to finish adapter install.
15. *jboss-cli -c --file=adapter-install.cli*
16. *jboss-cli -c --file=adapter-install-saml.cli*
17. *jboss-cli -c --command=:reload*

The Examples realm you imported has two users, *secure-user* and *admin-user*.  Both use "password" as the password.

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

You can run each example with the following URLs:

* <http://localhost:8080/jboss-war-app-html5>
* <http://localhost:8080/jboss-war-app-profile-html5>
* <http://localhost:8080/app-jee/>
* <http://localhost:8080/app-profile-jee>
* <http://localhost:8080/app-profile-jee-saml/>

Undeploy the Archive
--------------------

* Make sure you have started the JBoss EAP server as described above.
* Open a command prompt and navigate to the root directory of this quickstart.

mvn wildfly:undeploy
