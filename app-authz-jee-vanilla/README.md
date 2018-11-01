app-authz-jee-vanilla: JSP Application Using Fine-grained Authorization
================================================

Level: Beginner  
Technologies: JavaEE  
Summary: JSP Application Using Fine-grained Authorization  
Target Product: <span>Keycloak</span>, <span>WildFly</span>  
Source: <https://github.com/keycloak/keycloak-quickstarts>  


What is it?
-----------

The `app-authz-jee-vanilla` quickstart demonstrates how to enable fine-grained authorization to a Java EE application and use the
default authorization settings to protect all resources in the application.

By default, all resources are protected using a very simple JavaScript-Based Policy that always grant access to any resource.

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

When you enable authorization services for a client application, <span>Keycloak</span> automatically creates a few default settings for your client:

* Click the `Resources` tab and you'll see a single `Default Resource` resource representing all resources in your application. Note that the `URI` for this resource
is defined as `/*`.
* Click the `Policies` tab and you'll see a single `Default Policy` policy. This policy is using a rule written in JavaScript to decide whether or not access should be granted.
By default, this policy always evaluate to a GRANT, which means that access  will always be granted.
* Click `Permissions` tab and you'll see a single 'Default Permission' permission. This permission associates the resource you want to protect (e.g.: Default Resource) and
the policies that must be applied when someone request access to the resource. In this case, only `Default Policy` is evaluated when
checking permissions for the resource.

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

You can access the application with the following URL: <http://localhost:8080/app-authz-vanilla>.

If you want to play around, try the following steps:

* Create a scope, define a policy and permission for it, and test it on the application side. Can the user perform an action (or anything else represented by the scope you created)?

* Create different types of policies such as role-based, user-based, time-based, aggregated policies, or rule-based, and associate these policies with the Default Permission.

* Apply multiple policies to the Default Permission and test the behavior. For example, combine multiple policies and change the Decision Strategy accordingly.

For more information, please consult the Authorization Services documentation.

Undeploy the Quickstart
--------------------

1. Open a terminal and navigate to the root of the <span>Keycloak</span> server directory.

2. The following shows the command to undeploy the quickstart:

   ````
   mvn wildfly:undeploy
   ````
