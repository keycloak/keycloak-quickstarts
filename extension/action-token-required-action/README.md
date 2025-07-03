action-token: Using Action Token to Incorporate External App Call into Authentication Flow
==========================================================================================

Level: Intermediate
Technologies: JavaEE
Summary: Example of cooperation of Action Tokens and Required Actions
Target Product: Keycloak  
Source: <https://github.com/keycloak/keycloak-quickstarts>  


What is it?
-----------

This example shows how to invoke external application within the authentication
flow. It is implemented by cooperation of required action and action token:

1. During authentication, a required action that mandates cooperation with
   external application is invoked.

2. This required action prepares an action token for the current authentication
   session and redirects to the application, passing the action token along.

3. The application does whatever it is suited for (in this example, lets the
   user set values of two attributes).

4. Application uses the action token obtained in Step 2. to return back to
   authentication flow, providing the required action with its own signed token
   containing values entered by the user.

5. The handler handling that action token takes values of the fields and sets
   the attributes of authenticating user accordingly.


System Requirements
-------------------

You need to have <span>Keycloak</span> running. It is recommended to use Keycloak 26 or later.

All you need to build this project is Java 17 (Java SDK 17) or later and Maven 3.6.3 or later.


Build and Deploy the Quickstart
-------------------------------

To build the provider, run the following maven command:

   ````
   mvn -Pextension -DskipTests clean install
   ````

To install the provider, copy the `target/action-token-req-action-example.jar` JAR file to the `providers` directory of the server distribution.

Finally, start the server as follows:

    ```
    kc.[sh|bat] start-dev --http-port=8180 \
      --spi-action-token-handler--external-app-reqaction-notification--hmac-secret=aSqzP4reFgWR4j94BDT1r+81QYp/NYbY9SBwXtqV1ko= \
      --spi-required-action--redirect-to-external-application--external-application-url=http://127.0.0.1:8080/action-token-responder-example/external-action.jsp?token={TOKEN}
    ```

If you see this startup command, you can notice the last two configuration parameters, which are used for
a configuration of two custom SPIs implemented in this example:

 *  `external-app-reqaction-notification` action token handler is given a secret key that
    is used in step 5 to verify that the invocation comes from the correct app.

 *  `redirect-to-external-application` required action is provided with a URL
    where it should redirect the requests to when invoked as part of authentication
    flow. This URL contains special string `{TOKEN}` that is replaced with
    URL with action token. That URL is used by the external application to
    redirect back to Keycloak once its own flow is completed.

NOTE: When using Keycloak 26.2 or older, you may need to use the parameter names without `--` inside the parameter names. So something like
`--spi-action-token-handler-external-app-reqaction-notification-hmac-secret` and `--spi-required-action-redirect-to-external-application-external-application-url` .

NOTE: In production environment, you don't need to use the "confidential" parameters sent in the server startup command, which in this case
applies especially for the `hmac-secret` configuration parameter. It might be better to use configuration properties file for it, or even use
the Keycloak Vault capabilities. See the Keycloak documentation for more details about provider options and for the details about how to use the vault.

Note that you need to deploy the responder application into WildFly. A sample responder
application is part of the tests, so you first need to run the tests in order to have the WAR archive
with the application, which would then be deployed to the Wildfly server. See next steps for the details.

Integration test of the Quickstart
----------------------------------

1. Make sure you have an Keycloak server running with an admin user in the `master` realm or use the provided docker image. Also make sure that server
   was started with the parameters as described above and `action-token-req-action-example` is deployed to the server as described above
2. You need to have Chrome browser installed and updated to the latest version
3. Run `mvn -Pextension clean install`

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
cd target
export WILDFY_VERSION=`ls -d wildfly-*`
cp -r $WILDFY_VERSION /tmp/
cp deployments/wildfly_action-token-responder-example_action-token-responder-example.war /tmp/$WILDFY_VERSION/standalone/deployments/action-token-responder-example.war
cd /tmp/$WILDFY_VERSION/bin
./standalone.sh
```

Enable the Provider for a Realm
-------------------------------
Open [Keycloak admin console](http://localhost:8180/admin)
Login to the Keycloak Admin Console and go to the Authentication section,
Required Actions tab. You should now see your deployed required action `Redirect to external application`.
You can enable required action by switching `Enabled` to `ON`. This will now enable
the provider for the 'master' realm. Now you can set the required actions as
a default action (mandatory for any new user) or mandate user to execute this
action via admin interface. You can do that by click to some user in the admin console and make sure
that required action `redirect to external application` is added to `Required user actions`.

If you logout and login as a new user or a user you mandated to execute that
action (depending on the option you choose above, you would be redirected to
the external application requiring you to fill a form, whose values will be sent
back to Keycloak upon completion. There they will be stored as user attributes
and authentication flow will continue.

More Information
----------------
The Action Token SPI and how you can use Java EE to implement it is covered in detail in our server developer guide.
