authenticator: Example custom authenticator
========================================================

Level: Beginner  
Summary: Example custom authenticator  
Target Product: <span>Keycloak</span>  
Source: <https://github.com/keycloak/keycloak-quickstarts>


What is it?
-----------

This is an example of the Authenticator SPI implemented a custom authenticator. It allows the user to create a secret
question that is prompted when a user logs in to a new machine. The example does not aim to provide a realistic 
authentication mechanisms and should not be leveraged in production.


System Requirements
-------------------

You need to have <span>Keycloak</span> running. It is recommended to use Keycloak 24 or later.

All you need to build this project is Java 17 (Java SDK 17) or later and Maven 3.6.3 or later.

Build and Deploy the Quickstart
-------------------------------

To build the provider, run the following maven command:

   ````
   mvn -Pextension clean install -DskipTests=true
   ````

To install the provider, copy the target/authenticator-example.jar file to the `providers` directory of the server distribution.

Finally, start the server as follows:

    kc.[sh|bat] start-dev

Enable the Provider for a Realm
-------------------------------
It is not recommended to try this out with the master realm, so either use an existing test realm or create a new one.
If you create a new realm you must also register at least one user in the realm.

To enable the custom authenticator the first step is to create a custom authentication flow where it can be registered.
Login to the <span>Keycloak</span> Admin Console and got to the Authentication tab. Select the `browser` flow and under
`Action` click on `Duplicate`. Leave the name and description as is, and click on `Duplicate` to create the custom 
authentication flow.

Next step is to add the authenticator to the custom flow. On the row named `Copy of browser forms` click on the '+'
symbol and select `Add step`. Enter `secret` in the search box to find the custom authenticator. Select `Secret question`
and click `Add`. When added it will not be required initially, so find the row for `Secret Questions` and change
the requirement to `Required`.

Next step is to bind the custom flow as the default browser flow. Click on `Action` and select `Bind flow`, 
choose the binding type `Browser flow` and click on `Save`.

Final step is to enable the required action that is used by users to enter the answer needed to login. Click on
`Authentication` then on `Required actions`. Next to `Secret Question` click on the toggle for `Enabled` to turn on
the required action.

Now everything is configured and ready to try out, and you can try to login to the realm. The first time the user logs
in the user will be prompted to provide an answer to a question. The user will be prompted the question again when 
using a new machine or closing the browser.