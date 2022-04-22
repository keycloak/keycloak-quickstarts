authz-js-policies: Example of JavaScript authorization policy deployable to Keycloak server
===========================================================================================

Level: Intermediate
Technologies: JavaEE
Summary: Example of JavaScript authorization policy
Target Product: Keycloak  
Source: <https://github.com/keycloak/keycloak-quickstarts>  


What is it?
-----------

TODO

System Requirements
-------------------

You need to have Keycloak 18.0.Final running.

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.3.3 or later.


Build and Deploy the Quickstart
-------------------------------

This module contains few JavaScript authorization policies, which can be deployed to Keycloak server. TODO: More description...

To deploy the provider to Keycloak server, run the following maven command (optionally
specifying Keycloak management port with `-Dkeycloak.management.port=_port_`):

    ````
    mvn -Pwildfly-managed clean wildfly:deploy
    ````

If you want to play with and modify the example, simply rerun the maven deploy
command above and the new version will be hot deployed.

When Keycloak running on Quarkus, you can copy the JAR with the providers to the "providers" directory:

```
mvn clean install
cp -r target/authz-js-policies.jar $KEYCLOAK_HOME/providers/
```
Using Java 15 or later
----------------------
Java 15 or later does not contain any JavaScript engine in itself by default. However script engine is required for the javascript
policies used inside this quickstart. This means that if your Keycloak server is on Java 15 or later, you need to perform some additional
steps.

Using Java 15 or later on WildFly
---------------------------------
When using Keycloak distribution on WildFly, you can build this project with Java 15 or later and `legacy` property. Then make sure
to copy the additional module with the Nashorn JavaScript engine to your Wildfly server with the commands like these:

```
mvn clean install -Dlegacy
cp -r target/keycloak-server-legacy-copy/* $KEYCLOAK_HOME/
``` 

Using Java 15 or later on Quarkus
---------------------------------
When using Keycloak distribution on Quarkus, you can build this project with Java 15. Then make sure to copy the additional
module with the Nashorn JavaScript engine and it's dependencies to your Quarkus server `providers` directory with the commands like these:

```
mvn clean install
cp -r target/keycloak-server-copy/* $KEYCLOAK_HOME/
```
Then restart your Keycloak on quarkus and rebuild/reaugment it.


Enable the Provider for a Realm
-------------------------------
TODO

Integration test of the Quickstart
----------------------------------

TODO

More Information
----------------
TODO