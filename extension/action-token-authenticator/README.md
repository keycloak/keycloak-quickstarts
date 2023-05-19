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

You need to have <span>Keycloak</span> running. It is recommended to use Keycloak 22 or later.

All you need to build this project is Java 11 (Java SDK 11) or later and Maven 3.6.3 or later.


Build and Deploy the Quickstart
-------------------------------

To build the provider, run the following maven command:

   ````
   mvn clean install
   ````

To install the provider, copy the `target/action-token-example.jar` JAR file to the `providers` directory of the server distribution.

Finally, start the server as follows:

    ```
    kc.[sh|bat] start-dev --http-port=8180 --spi-action-token-handler-external-app-notification-hmac-secret=aSqzP4reFgWR4j94BDT1r+81QYp/NYbY9SBwXtqV1ko=
    ```

If you see this startup command, you can notice the last configuration parameter, which is used for
a configuration of a single custom SPI implemented in this example:

 *  `external-app-notification` action token handler is given a HMAC secret key that
    is used in step 5 to verify that the invocation comes from the correct app.

NOTE: In production environment, you don't need to use the "confidential" parameters sent in the server startup command. It might be better
to use configuration properties file for it, or even use the Keycloak Valve capabilities. See the Keycloak documentation for more details about provider
options and for the details about how to use the valve.

The custom authenticator is configured in admin console within the flow that uses it:

 *  `external-application-authenticator` authenticator is provided with a URL
    where it should redirect the requests to when invoked as part of authentication
    flow. This URL contains special string `{TOKEN}` that will be replaced programaticaly in runtime with
    URL with action token. That URL is used by the external application to
    redirect back to Keycloak once its own flow is completed.

Note that you need to deploy the responder application into WildFly. A sample responder
application is part of the tests, so you first need to run the tests in order to have the WAR archive
with the application, which would then be deployed to the Wildfly server. See next steps for the details.

Integration test of the Quickstart
----------------------------------

1. Make sure you have an Keycloak server running with an admin user in the `master` realm or use the provided docker image. Also make sure that server
was started with the parameters as described above 
2. You need to have Chrome browser installed and updated to the latest version
3. Run `mvn clean install -Djakarta`

After running the above command, the WAR file will be located in
`target/deployments/wildfly_action-token-responder-example_action-token-responder-example.war`.

Among other things, the test did 2 things:
- Deployed the WAR application to the Wildfly server, then started this Wildfly server with the example application. After the test, it stopped the Wildfly server.
- Imported new realm and configured authentication flows in it to contain new authenticator.

We will describe how you can do those steps manually in your environment.

Prepare and start Wildfly server
----------------------
You can download latest Wildfly server. If you run the mvn command as described above, you can already have one in the `target` directory.
We also need to deploy simple WAR application to it and start the server. In Linux, the commands to do all of that could be for example like this:

```
export WILDFY_VERSION=wildfly-28.0.0.Beta1
cp -r target/$WILDFY_VERSION /tmp/
cp target/deployments/wildfly_action-token-responder-example_action-token-responder-example.war /tmp/$WILDFY_VERSION/standalone/deployments/action-token-responder-example.war
cd /tmp/$WILDFY_VERSION/bin
./standalone.sh
```

Enable the Provider for a Realm
-------------------------------
Open [Keycloak admin console](http://localhost:8180/admin)
Login to the Keycloak Admin Console and go to the `Authentication` section,
`Flows` tab. Click to `browser` flow and then click in the right top corner on `Action` -> `Duplicate`. In the new flow named `Copy of Browser`, 
you can find `Copy of browser forms` subflow. Then add a new execution called `External Application
Authenticator` to this subflow and set its requirement to `REQUIRED`. This will now enable
the provider for the `master` realm. Now you can alter the authenticator
configuration by selecting "Config" from "Actions" menu. However no configuration change is needed as the default parameters
should work fine for the purpose of this example.

Finally you need to "Bind" this flow by click `Action` -> `Bind flow` in the right top corner and bind it as `Browser flow`.

If you logout and login again, during the authentication flow you would be
redirected to the external application requiring you to fill a form, whose values
will be sent back to Keycloak upon completion. There they will be stored as user
attributes and authentication flow will continue.

More Information
----------------
The Action Token SPI and how you can use Java EE to implement it is covered in detail in our server developer guide.
