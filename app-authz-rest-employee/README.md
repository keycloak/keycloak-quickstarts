app-authz-rest-employee: SpringBoot REST Service Protected Using Keycloak Authorization Services
===================================================

Level: Beginner
Technologies: SpringBoot
Summary: SpringBoot REST Service Protected Using Keycloak Authorization Services
Target Product: Keycloak
Source: <https://github.com/keycloak/Keycloak-quickstarts>


What is it?
-----------

The `app-authz-rest-employee` quickstart demonstrates how to protect a SpringBoot REST service using Keycloak Authorization Services.

This quickstart provides a RESTful API exposing a single endpoint to obtain information about employees. Employees
can only obtain information about themselves, but access is not granted if trying to obtain information about other employee.

However, users granted with a role `people-manager` are also allowed to access information about any employee.

The endpoint is available at:

```$bash
http://localhost:8080/api/{employee}
```

Where `{employee}` should be replaced by the employee's username, such as `/api/alice`.

You can use two distinct users to access this application:

|Username|Password|Roles|
|---|---|---|
|alice|alice|user|
|jdoe|jdoe|user, people-manager|

The quickstart demonstrates how to use Keycloak Authorization Services to:

* Pass information about the execution environment to policies in order to evaluate permissions for a resource
* Use a JavaScript-based policy to define constraints based on the execution environment
* Mix different policies and define how permissions should be evaluated

System Requirements
-------------------

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later.


Configuration in Keycloak
-----------------------

Prior to running the quickstart you need to create a `realm` in Keycloak with all the necessary configuration to deploy and run the quickstart.

The following steps show how to create the realm required for this quickstart:

* Open the Keycloak Admin Console
* In the top left corner dropdown menu that is titled `Master`, click `Add Realm`. If you are logged in to the master realm this dropdown menu lists all the realms created.
* For this quickstart we are not going to manually create the realm, but import all configuration from a JSON file. Click on `Select File` and import the [config/quickstart-realm](config/quickstart-realm).
* Click `Create`

The steps above will result on a new `spring-boot-quickstart` realm.

Build and Run the Quickstart
-------------------------------

Make sure your Keycloak server is running on <http://localhost:8180/>. For that, you can start the server using the command below:

   ````
   cd {KEYCLOAK_HOME}/bin
   ./standalone.sh -Djboss.socket.binding.port-offset=100
   
   ````

If your server is up and running, perform the following steps to start the application:

1. Open a terminal and navigate to the root directory of this quickstart.

2. The following shows the command to run the application:

   ````
   mvn spring-boot:run

   ````

Obtaining an OAuth2 Access Token
---------------------

First thing, your client needs to obtain an OAuth2 access token from a Keycloak server for user `alice`. You can use the same command to obtain tokens
on behalf of user `jdoe`, just make sure to change both `username` and `password` request parameters.

```bash
 export access_token=$(\
    curl -X POST http://localhost:8180/auth/realms/spring-boot-quickstart/protocol/openid-connect/token \
    -H 'Authorization: Basic YXBwLWF1dGh6LXJlc3QtZW1wbG95ZWU6c2VjcmV0' \
    -H 'content-type: application/x-www-form-urlencoded' \
    -d 'username=alice&password=alice&grant_type=password' | jq --raw-output '.access_token' \
 )
```

Accessing Protected Resources using an OAuth2 Access Token
---------------------

The most simple way to invoke resources protected by a policy enforcer is sending an OAuth2 Access Token. If you successfully obtained an OAuth2 Access Token in the previous section, 
you can access resources in this application as follows:

```bash
curl -v -X GET \
  http://localhost:8080/api/alice \
  -H "Authorization: Bearer "$access_token
```

User `alice` should be able to access information about herself and you should get a response as follows:

```$bash
{
    "name": "alice"
}
```

User `alice` can not access information about `jdoe` and you should get an access denied.

```bash
curl -v -X GET \
  http://localhost:8080/api/jdoe \
  -H "Authorization: Bearer "$access_token
```

User `jdoe` is granted with role `people-manager` and you should be able to access information about any employee,
including `alice`. For that, make sure to obtain an access token for user `jdoe` and then send a request to:

```$bash
curl -v -X GET \
  http://localhost:8080/api/alice \
  -H "Authorization: Bearer "$access_token
```

What to do next ?
----------------------------------

* Try to remove the policy `Only People Manager Policy` from the permission `Employee Permission` and you`ll see that `jdoe` is no longer granted with access to query information about `alice`.
* Try to change `Employee Policy`, a JavaScript-based policy, to change behavior.

Integration test of the Quickstart
----------------------------------  

1. Make sure you have an Keycloak server running with an admin user in the `master` realm or use the provided docker image
2. Be sure to set the `TestHelper.keycloakBaseUrl` in the `createArchive` method (default URL is localhost:8180/auth).
3. Set accordingly the correct url for the `keycloak.auth-server-url` in the test [application.properties](src/test/resources/application.properties).
4. Run `mvn test -Pspring-boot`
