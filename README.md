# RH-SSO Quickstarts

What is it?
-----------
TODO improve description

With the exception of app-html5 and app-profile-html5, all examples are deployed as a WAR.  These examples assume that a RH-SSO server is running on localhost:8180 and a EAP server is running on localhost:8080.

If you want to change the address of the RH-SSO server, edit the keycloak.json file in each example's WEB-INF directory.
If you want to change the address of the EAP server, edit each example's client detail in the UI of the RH-SSO server.

Requirements and settings
-------------------------

If you are in a Unix compatible environment you can use the following shell commands 'as-is'.
If you are on Windows, you'll have to convert these instructions - for example, use 'xcopy', and 'rmdir' instead of 'cp', and 'rm', or perform the operations through UI using Windows Explorer, Windows Commander or similar.

Note that running scripts have '.bat' extension on Windows rather than '.sh'.


1. Download RH-SSO Quickstarts archive, and its dependencies.

   We'll assume that you have created a working directory under your $HOME where we'll unpack Quickstarts, and servers:
   ```
   export DEMOS=~/RH-SSO
    mkdir $DEMOS
   ```

   We'll also assume that all the downloads are available in your ~/Downloads directory.

   To unpack the Quickstarts use the following:
   ```
   cd $DEMOS
   unzip ~/Downloads/RH-SSO-7.0.0-quickstarts.zip
   ```

   In order to build Quickstarts you'll need the RH-SSO maven repository, and EAP 7 maven repository as well.
   Both can be downloaded as .zip files from RH Customers site.

   They can be unpacked, and copied into your existing local maven repository with the following commands:

   ```
   cd ~/.m2
   unzip ~/Downloads/RH-SSO-7.0.0-maven-repository.zip
   cp -rf RH-SSO-7.0.0.GA-maven-repository/maven-repository/* repository/
   rm -rf RH-SSO-7.0.0.GA-maven-repository

   unzip ~/Downloads/jboss-eap-7.0.0-maven-repository.zip
   cp -rf jboss-eap-7.0.0.GA-maven-repository/maven-repository/* repository/
   rm -rf jboss-eap-7.0.0.GA-maven-repository
   ```

   Note: If you have not used maven before you may first need to create local maven repository directory yourself:

   ```
   mkdir -p ~/.m2/repository
   ```

2. Download and unzip the plain **RH-SSO** distribution.

   Once downloaded, you simply unpack it:
   ```
   cd $DEMOS
   unzip ~/Downloads/RH-SSO-7.0.0.GA.zip
   ```

3. Download and unzip EAP 6.4 / EAP 7

   Once downloaded, you simply unpack it:
   ```
   cd $DEMOS
   unzip ~/Downloads/jboss-eap-7.0.0.zip
   ```

   Or for EAP 6.4 use:
   ```
   cd $DEMOS
   unzip ~/Downloads/jboss-eap-6.4.0.zip
   ```

4. Download the OIDC adapter for EAP 6 / EAP 7

   Once downloaded, unzip it on top of EAP (it's ok to overwrite any existing files):
   ```
   cd $DEMOS/jboss-eap-7.0
   unzip ~/Downloads/RH-SSO-7.0.0.GA-eap7-adapter.zip
   ```
   Or for EAP 6.4 use:
   ```
   cd $DEMOS/jboss-eap-6.4
   unzip ~/Downloads/RH-SSO-7.0.0.GA-eap6-adapter.zip
   ```

5. Download the SAML adapter for EAP 6 / EAP 7

   Once downloaded, unzip it on top of EAP (it's ok to overwrite any existing files):
   ```
   cd $DEMOS/jboss-eap-7.0
   unzip ~/Downloads/RH-SSO-7.0.0.GA-saml-eap7-adapter.zip
   ```

   Or for EAP 6.4 use:
   ```
   cd $DEMOS/jboss-eap-6.4
   unzip ~/Downloads/RH-SSO-7.0.0.GA-saml-eap6-adapter.zip
   ```


6. Start RH-SSO server on port 8180

   ```
   cd $DEMOS/RH-SSO-7.0.0.GA/bin
   ./standalone.sh -Djboss.socket.binding.port-offset=100
   ```

7. Open Admin Console in your browser, and setup initial admin user

   In your browser go to <http://localhost:8180/auth/>
   For the purposes of this demonstration we'll assume that you set the admin's username to 'admin'.

8. Login as admin

   In your browser go to <http://localhost:8180/auth/admin/index.html> and login as admin.

9. Import the realm required by Quickstarts

   In the upper lefthand corner click on 'Master', and choose 'Add Realm'.
   Click 'Select File" and choose *examples-realm.json* from the RH-SSO Quickstarts directory.
   Click 'Create'.

   This will create a new 'Examples' realm which has two users, *secure-user* and *admin-user*.  They both use 'password' as a password.


10. Start EAP server on port 8080

   Use a new Terminal window:

   ```
   export EAP7=$DEMOS/jboss-eap-7.0
   cd $EAP7/bin
   ./standalone.sh
   ```

   Or for EAP 6.4 use:
   ```
   export EAP6=$DEMOS/jboss-eap-6.4
   cd $EAP6/bin
   ./standalone.sh
   ```

11. Install adapters into a running EAP

   With EAP running, open a new command prompt and perform the following:

   ```
   cd $EAP7/bin
   ```
   Or for EAP 6.4 use:
   ```
   cd $EAP6/bin
   ```

   Then use jboss-cli:
   ```
   ./jboss-cli.sh -c --file=adapter-install.cli
   ./jboss-cli.sh -c --file=adapter-install-saml.cli
   ./jboss-cli.sh -c --command=:reload
   ```


12. At this point your environment is ready to deploy Quickstarts.


System requirements
-------------------

You need Java 8 (Java SDK 1.8), and Apache Maven 3.1.1 or later to build this project. See Configure Maven for JBoss EAP 7 to make sure you are configured correctly for testing the quickstarts.


Build and Deploy the Quickstarts
--------------------------------

1. Make sure you have started, and configured the JBoss EAP server as described above in steps 3-5, and 10-11.
2. Open a new command prompt and navigate to the root directory of Quickstarts.
   ```
   cd $DEMOS/RH-SSO-7.0.0.GA-quickstarts
   ```

3. Build and deploy the archive:

   ```
   mvn clean install
   ```

   To deploy to EAP 7 you can then simply execute:

   ```
   mvn wildfly:deploy-only
   ```

   This will deploy all the quickstarts to EAP instance running on port 8080.
   You can find the built .war for each quickstart under *target/QUICKSTART_NAME.war*.


   To deploy to EAP 6 you will have to do it manually. For example by copying to EAP deployment directory:

   ```
   cp app-jee/target/app-jee.war $EAP6/standalone/deployments
   cp app-profile-jee/target/app-profile-jee.war $EAP6/standalone/deployments
   cp app-profile-jee-saml/target/app-profile-jee-saml.war $EAP6/standalone/deployments
   cp app-profile-jee-vanilla/target/app-profile-jee-vanilla.war $EAP6/standalone/deployments
   cp service-jaxrs/target/service-jaxrs.war $EAP6/standalone/deployments
   cp war-app-html5/target/app-html5.war $EAP6/standalone/deployments
   cp war-app-profile-html5/target/app-profile-html5.war $EAP6/standalone/deployments
   ```


   You can also deploy individual example by changing into specific subdirectory, and running the same 'mvn' command.


Access the application
----------------------

You can access individual examples on the following URLs:

* <http://localhost:8080/jboss-war-app-html5>
* <http://localhost:8080/jboss-war-app-profile-html5>
* <http://localhost:8080/app-jee/>
* <http://localhost:8080/app-profile-jee>
* <http://localhost:8080/app-profile-jee-saml/>


Undeploy the Archive
--------------------

* Make sure you have started the JBoss EAP server as described above.
* Open a command prompt and navigate to the root directory of this quickstart.
```
cd $DEMOS/RH-SSO-7.0.0.GA-quickstarts
```
* Undeploy all application archives
For EAP 7 you can simply do:
```
mvn wildfly:undeploy
```
You can also undeploy individual example by changing into specific subdirectory, and running the same 'mvn' command.

For EAP 6 you'll need to do it manually. For example by removing .wars from EAP deployments directory:

```
rm $EAP6/standalone/deployments/*.war
```
