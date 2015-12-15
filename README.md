# keycloak-examples

With the exception of app-html5 and app-profile-html5, all examples are deployed as a WAR.  These examples assume that a Keycloak server is running on localhost:8180 and a WildFly server is running on localhost:8080.

If you want to change the address of the Keycloak server, edit the keycloak.json file in each example's WEB-INF directory.
If you want to change the address of the WildFly server, edit each example's client detail in the UI of the Keycloak server.

To install and run all the WAR-based examples:

1. Download this keycloak-examples repository.
2. [Download](http://keycloak.jboss.org/keycloak/downloads.html) and unzip the plain **keycloak** distribution.  Do not download the demo dist.
3. [Download](http://wildfly.org/downloads/) an unzip WildFly.
4. [Download](http://keycloak.jboss.org/keycloak/downloads.html?dir=0%3Dadapters/keycloak-oidc%3B) the Keycloak OIDC adapter for WildFly.  Unzip this adapter on top of WildFly.
5. [Download](http://keycloak.jboss.org/keycloak/downloads.html?dir=0%3Dadapters/saml%3B) the Keycloak SAML adapter for WildFly.  Unzip this adapter on top of WildFly.  It's OK to overwrite modules.
6. *cd &lt;Keycloak Home&gt;/bin*
7. *standalone(.bat or .sh) -Djboss.socket.binding.port-offset=100*
8. In your browser, go to <http://localhost:8180/auth/admin/index.html>
9. Login with admin/admin and change the password when prompted.
10. In the upper lefthand corner, click on Master->Add Realm
11. Click "Select File" and choose *examples-realm.json* from the root of the keycloak-examples repo from step 1.
12. *cd &lt;WildFly Home&gt;/bin*
13. *standalone(.bat or .sh)*
14. With WildFly running, open a new command prompt and *cd &lt;WildFly Home&gt;/bin* again.  Then execute the following three commands to finish adapter install.
15. *jboss-cli -c --file=adapter-install.cli*
16. *jboss-cli -c --file=adapter-install-saml.cli*
17. *jboss-cli -c --command=:reload*
18. At the root of the keycloak-examples repo, run *mvn wildfly:deploy*

The Examples realm you imported has two users, *secure-user* and *admin-user*.  Both use "password" as the password.

You can run each example with the following URLs:

* <http://localhost:8080/app-html5/>
* <http://localhost:8080/app-profile-html5/>
* <http://localhost:8080/app-jee/>
* <http://localhost:8080/app-profile-jee/>
* <http://localhost:8080/app-profile-jee-saml/>
