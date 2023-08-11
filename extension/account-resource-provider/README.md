account-resource-provider: Learn to replace the Account Console
===================================================

Level: Expert
Technologies: Java, JAX-RS
Summary: Recreate the v1 account console using the AccountResourceProvider extension point
Target Product: <span>Keycloak</span>
Source: <https://github.com/keycloak/keycloak-quickstarts>


What is it?
-----------

The `account-resource-provider` quickstart demonstrates how to create an `AccountResourceProvider`
implementation. This is usually done in service of a totally custom account theme that has custom
requirements for endpoints served at the `/realms/{realm}/account` root.

System Requirements
-------------------

You need to have <span>Keycloak</span> running. It is recommended to use Keycloak 22 or later.

All you need to build this project is Java 11 (Java SDK 11) or later and Maven 3.6.3 or later.

It is also recommended that you read about Keycloak themes in the Server Developer guide. 


Configuration in <span>Keycloak</span>
-----------------------

To build the provider, run the following maven command:

   ````
   mvn clean install -Pextension -DskipTests
   ````

To install the provider, copy the `target/keycloak-account-v1.jar` JAR file to the `providers` directory of the server distribution.

Finally, start the server as follows:

    ```
    kc.[sh|bat] start-dev
    ```
1. Open Keycloak Admin Console.
2. Go to the ``Realm Settings-->Themes`` tab. 
3. Set Account Theme to ``account-v1``
4. Go to the account console.

Access the Quickstart
---------------------

You can access the account console with a URL like: <http://localhost:8080/realms/master/account>.

Integration test of the Quickstart
----------------------------------

1. Make sure you have an Keycloak server running with an admin user in the `master` realm or use the provided docker image. Your <span>Keycloak</span> should be listening on `http://localhost:8180`. You can archive this by running:

```
./kc.sh start-dev --http-port=8180
```

2. You need to have Chrome browser installed and updated to the latest version.
3. Run `mvn clean install -Pextension`
