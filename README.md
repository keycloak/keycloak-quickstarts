# keycloak-examples

To install and run all examples:

1. Download this keycloak-examples repository.
2. [Download](http://keycloak.jboss.org/keycloak/downloads.html) and unzip the **keycloak-demo** distribution.  The keycloak-demo distribution combines the Keycloak server with the adapter and subsystems that allow you to run Keycloak-secured applications.
2. In the unzipped directory, cd keycloak/bin.
3. Start the server by running standalone.sh or standalone.bat.
4. In your browser, go to <http://localhost:8080/auth/admin/index.html>
5. Login with admin/admin and change the password when prompted.
6. In the upper lefthand corner, click on Master->Add Realm
7. Click "Select File" and choose *examples-realm.json* from the root of the keycloak-examples repo from step 1.
8. At the root of the keycloak-examples repo, run *mvn wildfly:deploy*
 
The Examples realm you imported has two users, *secure-user* and *admin-user*.  Both use "password" as the password.

You can run each example with the folling URLs:

* <http://localhost:8080/app-html5/>
* <http://localhost:8080/app-profile-html5/>
* <http://localhost:8080/app-jee/>
* <http://localhost:8080/app-profile-jee/>
* <http://localhost:8080/app-profile-jee-saml/>
