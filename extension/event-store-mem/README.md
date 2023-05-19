event-store-mem: Event Store that stores events in memory
=========================================================

Level: Beginner  
Technologies: JavaEE  
Summary: Example of Event Store provider  
Target Product: Keycloak  
Source: <https://github.com/keycloak/keycloak-quickstarts>

What is it?
-----------

This example shows how to implement and deploy an event store provider that stores events in memory.
You can extend the server's functionality in this way by implementing your own provider. For more information see our server developer guide.

To configure and enable the event store provider manually add default provider to `eventsStore` SPI as `in-mem` as described below.
This is needed because `eventsStore` SPI can have only one "active" provider in Keycloak. And by default, the `eventsStore` provider is 
configured to store events in the Keycloak Storage DB. But for the illustration purpose, we will do it in this example to store them in memory only.

Then go to [Events Config](http://localhost:8180/admin/master/console/#/realms/master/events-settings) tab in the admin console and enable login events and admin events by a toggle buttons. You can configure what event types should be stored. Save the changes afterwards.

To apply changes in the configuration file restart the server.

When you have the example deployed and configured go to [Login Events](http://localhost:8180/admin/master/console/#/realms/master/events) tab in the admin console where you can see login events showed from the Event store provider.
Similarly in [Admin Events](http://localhost:8180/admin/master/console/#/realms/master/admin-events) tab you can see generated admin events. To generate an admin event you can for example create a user.

If you restart the server again all the events will be gone because they haven't been persisted in the database but stored only in memory.

Alternatively you can configure and deploy the example by maven command mentioned below.


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

To install the provider, copy the target/event-store-mem.jar JAR file to the `providers` directory of the server distribution.

Finally, start the server as follows:

    ```
    kc.[sh|bat] start-dev --http-port=8180 --spi-events-store-provider=in-mem
    ```

Then go to [Events Config](http://localhost:8180/admin/master/console/#/master/realm-settings/events) tab in the admin console and enable
login events and admin events by a toggle buttons `Save events` in the subtabs `User event settings` and `Admin event settings`. You can configure
what event types should be stored and expiration of events. Save the changes afterwards.

To apply changes in the configuration file restart the server.

When you have the example deployed and configured go to [Login Events](http://localhost:8180/admin/master/console/#/master/events/user-events) tab in the admin console where you can see login events showed from the Event store provider.
Similarly in [Admin Events](http://localhost:8180/admin/master/console/#/master/events/admin-events) tab you can see generated admin events. To generate an admin event you can for example create a user.

If you restart the server again all the events will be gone because they haven't been persisted in the database but stored only in memory.


Integration test of the Quickstart
----------------------------------

1. Make sure you have an Keycloak server running with an admin user in the `master` realm or use the provided docker image.
   Your <span>Keycloak</span> should be listening on `http://localhost:8180` and should have set `in-mem` as the default `eventsStore` provider.
   See in the previous section how the startup command for the server should look like.

2. You need to have Chrome browser installed and updated to the latest version.
3. Run `mvn clean install -Djakarta`


Undeploy the quickstart
-----------------------
Delete file `providers/event-store-mem.jar` and restart the server without option to set the default event store provider, which means Keycloak
will again use the default built-in Keycloak provider for storing events.

More Information
----------------
This particular example is discussed in detail in the Event Listener SPI chapter of the server developer guide.  Each line of code is dissected
to show you how the Event Listener SPI works.