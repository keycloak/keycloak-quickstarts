event-listener-sysout: Event Listener that prints events to System.out
======================================================================

Level: Beginner  
Technologies: JavaEE  
Summary: Example of Event Listener provider  
Target Product: Keycloak  
Source: <https://github.com/keycloak/keycloak-quickstarts>

What is it?
-----------

This example shows how to implement and deploy an event listener that writes events to System.out.
You can extend the server's functionality in this way by implementing your own provider. For more information see our server developer guide.

After the provider is deployed and registered to Keycloak, log out and log in back. When an event is generated it will be printed to System.out.
There is a possibility to exclude a specific event. See the tests for more details.   


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

To install the provider, copy the target/event-listener-sysout.jar JAR file to the `providers` directory of the server distribution.

Finally, start the server as follows:

    ```
    kc.[sh|bat] start-dev --http-port=8180 --spi-events-listener-sysout-exclude-events=CODE_TO_TOKEN,REFRESH_TOKEN
    ```

Then go to [Events Config](http://localhost:8180/admin/master/console/#/master/realm-settings/events) tab in the admin console and add `sysout` to Event Listeners.
Save the changes afterwards. 

Then you can do some user operations with admin user (like login/logout) or some admin operations (like create/update some objects in the admin console)
and see the events logged into the server log. The excluded events (in this example `CODE_TO_TOKEN` and `REFRESH_TOKEN`) won't be logged as they
are supposed to be excluded. For illustration purposes, there is only some message about excluded event added to server log.

You can try to exclude some other events instead to see how the things can be configured.

Integration test of the Quickstart
----------------------------------

1. Make sure you have an Keycloak server running with an admin user in the `master` realm or use the provided docker image. 
Your <span>Keycloak</span> should be listening on `http://localhost:8180` and should have excluded events configured (at least `CODE_TO_TOKEN).
See in the previous section how the startup command for the server should look like.

2. You need to have Chrome browser installed and updated to the latest version.
3. Run `mvn clean install -Djakarta`

Undeploy the quickstart
-----------------------
Remove `sysout` listener from your realm in the admin console, delete file `providers/event-listener-sysout.jar` and restart the server.

More Information
----------------
This particular example is discussed in detail in the Event Listener SPI chapter of the server developer guide.  Each line of code is dissected
to show you how the Event Listener SPI works.
