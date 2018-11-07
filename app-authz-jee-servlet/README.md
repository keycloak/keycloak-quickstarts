app-authz-jee-servlet: Servlet Application Using Fine-grained Authorization
================================================

Level: Beginner  
Technologies: JavaEE  
Summary: Servlet Application Using Fine-grained Authorization  
Target Product: <span>Keycloak</span>, <span>WildFly</span>
Source: <https://github.com/keycloak/keycloak-quickstarts>  


What is it?
-----------

The `app-authz-jee-servlet` quickstart demonstrates how to enable fine-grained authorization to a Java EE application in order to protect
specific resources and build a dynamic menu based on the permissions obtained from a <span>Keycloak</span> Server.

For this application, users can be regular users, premium users or administrators, where:

* Regular users have very limited access.
    * They are allowed to access the main page ([src/main/webapp/index.jsp](src/main/webapp/index.jsp)) and the page that generates a dynamic menu ([src/main/webapp/protected/dynamicMenu.jsp](src/main/webapp/protected/dynamicMenu.jsp))
* Premium users have access to the *premium area*
    * They are allowed to access a specific set of pages for premium users ([src/main/webapp/protected/premium](src/main/webapp/protected/premium)
* Administrators have access to the *administration area*
    * They are allowed to access a specific set of pages for administrators ([src/main/webapp/protected/admin](src/main/webapp/protected/admin))

The dynamic menu is built based on the permissions obtained from the server and using the `AuthorizationContext` object to
determine the resources and scopes the user is allowed to access.

You'll also learn how to use the `AuthorizationContext` object to obtain permissions granted by the server and perform additional checks in the application.

System Requirements
-------------------

If you are deploying the application as a WAR you need to have <span>WildFly 10</span> running.

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later.

Configure the Client Adapter
----------------------------------

Before configuring the adapter you need to create a `realm` in <span>Keycloak</span> with all the necessary configuration to deploy and run the quickstart.

The following steps show how to create the realm required for this quickstart:

* Open the <span>Keycloak</span> admin console
* In the top left corner dropdown menu that is titled Master, click Add Realm. If you are logged in to the master realm this dropdown menu lists all the realms created.
* For this quickstart we are not going to manually create the realm, but import all configuration from a JSON file. Click on `Select File` and import the [config/realm-import.json](config/realm-import.json).
* Click `Create`

The steps above will result on a new `quickstart` realm.

[NOTE]
If you deploy the application somewhere else change the hostname and port of the URLs accordingly.

Finally you need to configure the adapter, this is done by retrieving the adapter configuration file:

* Click on `Installation` in the tab for the client you created
* Select `Keycloak OIDC JSON`
* Click `Download`
* Move the file `keycloak.json` to the `config/` directory in the root of the quickstart
* (optional) By default, the policy enforcer responds with a 403 status code when the user lacks permission to access protected resources
on the resource server. However, you can also specify a redirection URL for unauthorized users.
To specify a redirection URL, edit the `keycloak.json` and replace the `policy-enforcer` configuration with the following:

    ````
      "policy-enforcer": {
          "on-deny-redirect-to" : "/authz-servlet/accessDenied.jsp"
        }
    ````

As an alternative you can configure the client adapter by copying [config/keycloak-example.json](config/keycloak-example.json) to `config/keycloak.json`.

Build and Deploy the Quickstart
-------------------------------

1. Open a terminal and navigate to the root directory of this quickstart.

2. The following shows the command to deploy the quickstart:

   ````
   mvn clean wildfly:deploy

   ````


Access the Quickstart
----------------------

You can access the application with the following URL: <http://localhost:8080/authz-servlet>.

If you want to play around, try the following steps:

* Add `user_premium` to `alice` and see if she is now able to access premium resources as well if the dynamic menu changes and display `Do user premium thing`
* Grant to `alice` the necessary permission to access administrative resources.
    * Open the <span>Keycloak</span> admin console and make sure the `authz-servlet` realm is selected
    * Click on `Clients` on the left-side menu and select `authz-servlet` client
    * Click on `Authorization` tab and then click the `Policies` tab
    * On the `Policies` tab, create a new `User-based Policy` by selecting `alice` user. Name this policy as `Only Alice Policy`
    * Click on `Permissions` tab and select `Administrative Resource Permission`
        * Add `Only Alice Policy` policy to the `Apply Policy` field
        * Change `Decision Strategy` from `Unanimous` to `Affirmative`. Click `Save`
    * Logout and log in again as `alice` user and try to access <http://localhost:8080/authz-servlet/protected/admin/onlyAdmin.jsp>
* Revoke `alice` administrative permissions by changing the `Only Alice Policy` logic
    * Click on `Policies` tab and select `Only Alice Policy`
        * Change `Logic` field from `Positive` to `Negative`. Click `Save`
    * Logout and log in again as `alice` user and try to access <http://localhost:8080/authz-servlet/protected/admin/onlyAdmin.jsp>

For more information, please consult the Authorization Services documentation.

Undeploy the Quickstart
--------------------

1. Open a terminal and navigate to the root of the <span>Keycloak</span> server directory.

2. The following shows the command to undeploy the quickstart:

   ````
mvn wildfly:undeploy
   ````
