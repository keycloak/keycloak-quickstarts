action-token: Using Action Token to Incorporate External App Call into Authentication Flow
==========================================================================================

Level: Intermediate
Technologies: JavaEE
Summary: Example of cooperation of Action Tokens and Authenticators
Target Product: Keycloak  
Source: <https://github.com/keycloak/keycloak-quickstarts>  


What is it?
-----------

This example shows how to invoke external application within the authentication
flow. It is implemented by cooperation of authenticator and action token:

1. During authentication, a custom authenticator that mandates cooperation with
   external application is invoked.

2. This authenticator prepares an action token for the current authentication
   session and redirects to the application, passing the action token along.

3. The application does whatever it is suited for, e.g. perform authentication
   of the user with some custom advanced credential type (in this example, lets the
   user set values of two attributes).

4. Application uses the action token obtained in Step 2. to return back to
   authentication flow, providing the authenticator with its own signed token
   containing values entered by the user.

5. The handler handling that action token takes values of the fields and sets
   the attributes of authenticating user accordingly.


System Requirements
-------------------

You need to have Keycloak 3.2.0.Final running.

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later.


Build and Deploy the Quickstart
-------------------------------

If you open the pom.xml file you'll see that the add-spi-configurations execution creates
a configuration of a single custom SPI implemented in this example:

 *  `external-app-notification` action token handler is given a secret key that
    is used in step 5 to verify that the invocation comes from the correct app.

The custom authenticator is configured in admin console within the flow that uses it:

 *  `external-application-authenticator` authenticator is provided with a URL
    where it should redirect the requests to when invoked as part of authentication
    flow. This URL contains special string `{TOKEN}` that is replaced with
    URL with action token. That URL is used by the external application to
    redirect back to Keycloak once its own flow is completed.

To deploy the provider to Keycloak server, run the following maven command (optionally
specifying Keycloak management port with `-Dkeycloak.management.port=_port_`):

    ````
    mvn -Pwildfly-managed clean wildfly:deploy
    ````

If you want to play with and modify the example, simply rerun the maven deploy
command above and the new version will be hot deployed.

Note that you need to deploy the responder application into WildFly. A sample responder
application is part of the tests and after running the above command will be located in
`target/deployments/wildfly_action-token-responder-example_action-token-responder-example.war`.

Enable the Provider for a Realm
-------------------------------
Login to the Keycloak Admin Console and go to the Authentication section,
Flows tab. Copy a flow and add a new execution called "External Application
Authenticator" and set its requirement to `REQUIRED`. This will now enable
the provider for the 'master' realm. Now you can alter the authenticator
configuration by selecting "Config" from "Actions" menu.

If you logout and login again, during the authentication flow you would be
redirected to the external application requiring you to fill a form, whose values
will be sent back to Keycloak upon completion. There they will be stored as user
attributes and authentication flow will continue.

Integration test of the Quickstart
----------------------------------

1. Make sure you have an Keycloak server running with an admin user in the `master` realm or use the provided docker image
2. Run `mvn test -Pwildfly-managed`

More Information
----------------
The Action Token SPI and how you can use Java EE to implement it is covered in detail in our server developer guide.
