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

You need to have Keycloak running with an initial user created. The example requires that Keycloak server is running on port 8180 (management port 10090). See the parent [README](https://github.com/keycloak/keycloak-quickstarts#start-the-keycloak-server) for more details.

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.3.3 or later.


Build and Deploy the Quickstart
-------------------------------

To build and deploy the example to Keycloak using a Maven run:

    mvn install -DskipTests wildfly:deploy
    
Alternatively you can deploy the example manually by copying the example's jar to:

    ````
    KEYCLOAK_HOME/standalone/deployments (for Linux)
    KEYCLOAK_HOME\standalone\deployments (for Windows)
    ````  
Then go to [Events Config](http://localhost:8180/auth/admin/master/console/#/realms/master/events-settings) tab in the admin console and add `sysout` to Event Listeners.
Save the changes afterwards. 


Integration test of the Quickstart
----------------------------------

1. Make sure you have a Keycloak server running with an `admin` user and `admin` password in the `master` realm
2. Run `mvn test -Pkeycloak-remote`


Undeploy the Quickstart
-----------------------

To undeploy the example using a Maven run:

    mvn clean wildfly:undeploy
    
Don't forget to remove *EventListener* spi from the *standalone.xml* if you want to deploy or test the example using the Maven commands above.
