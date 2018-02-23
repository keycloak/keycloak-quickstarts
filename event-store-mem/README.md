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

To configure and enable the event store provider manually add default provider to `eventsStore` SPI in `standalone/configuration/standalone.xml`:

    ````
    <spi name="eventsStore">
        <default-provider>in-mem</default-provider>
    </spi>
    ````
Then go to [Events Config](http://localhost:8180/auth/admin/master/console/#/realms/master/events-settings) tab in the admin console and enable login events and admin events by a toggle buttons. You can configure what event types should be stored. Save the changes afterwards.

To apply changes in the configuration file restart the server.

When you have the example deployed and configured go to [Login Events](http://localhost:8180/auth/admin/master/console/#/realms/master/events) tab in the admin console where you can see login events showed from the Event store provider.
Similarly in [Admin Events](http://localhost:8180/auth/admin/master/console/#/realms/master/admin-events) tab you can see generated admin events. To generate an admin event you can for example create a user.

If you restart the server again all the events will be gone because they haven't been persisted in the database but stored only in memory.

Alternatively you can configure and deploy the example by maven command mentioned below.


System Requirements
-------------------

You need to have Keycloak running with an initial user created. The example requires that Keycloak server is running on port 8180 (management port 10090). See the parent [README](https://github.com/keycloak/keycloak-quickstarts#start-the-keycloak-server) for more details.

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later.


Build and Deploy the Quickstart
-------------------------------

To build, configure and deploy the example to Keycloak run:

    mvn install -DskipTests wildfly:deploy wildfly:execute-commands
    
Alternatively you can deploy the example manually by copying the example's jar to:

    ````
    KEYCLOAK_HOME/standalone/deployments (for Linux)
    KEYCLOAK_HOME\standalone\deployments (for Windows)
    ````  


Integration test of the Quickstart
----------------------------------

1. Make sure you have a Keycloak server running with an `admin` user and `admin` password in the `master` realm
2. Run `mvn test -Pkeycloak-remote`


Undeploy the Quickstart
-----------------------

To undeploy the example run:

    mvn clean wildfly:undeploy

Don't forget to remove default *in-mem* provider from `standalone/configuration/standalone.xml` and restart the server if you want to revert back to *jpa* provider.
