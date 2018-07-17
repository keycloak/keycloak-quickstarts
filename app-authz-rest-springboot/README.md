app-authz-rest-springboot: SpringBoot REST Service Protected Using Keycloak Authorization Services
===================================================

Level: Beginner
Technologies: SpringBoot
Summary: SpringBoot REST Service Protected Using Keycloak Authorization Services
Target Product: Keycloak
Source: <https://github.com/keycloak/Keycloak-quickstarts>


What is it?
-----------

The `app-authz-rest-springboot` quickstart demonstrates how to protect a SpringBoot REST service using Keycloak Authorization Services.

This quickstart tries to focus on the authorization features provided by Keycloak Authorization Services, where resources are
protected by a set of permissions and policies defined in Keycloak and access to these resources are enforced by a policy enforcer(PEP)
that intercepts every single request sent to the application to check whether or not access should be granted.

In this application, there are three paths protected by specific permissions in Keycloak:

* **/api/{resource}**, where access to this resource is based on the evaluation of permissions associated with a resource **Default Resource** in Keycloak. Basically,
any user with a role *user* is allowed to access this resource. Examples of resource that match this path pattern are: "/api/resourcea" and "/api/resourceb".

* **/api/premium**, where access to this resource is based on the evaluation of permissions associated with a resource **Premium Resource** in Keycloak. Basically,
only users with a role *user-premium* is allowed to access this resource.

* **/api/admin**, where access to this path is based on the evaluation of permissions associated with a resource **Admin Resource** in Keycloak. Basically,
any user can access this resource as long as a specific request parameter is set.

You can use two distinct users to access this application:

|Username|Password|Roles|
|---|---|---|
|alice|alice|user|
|jdoe|jdoe|user, user-premium|

System Requirements
-------------------

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later.


Configuration in Keycloak
-----------------------

Prior to running the quickstart you need to create a `realm` in Keycloak with all the necessary configuration to deploy and run the quickstart.

The following steps show how to create the realm required for this quickstart:

* Open the Keycloak Admin Console
* In the top left corner dropdown menu that is titled `Master`, click `Add Realm`. If you are logged in to the master realm this dropdown menu lists all the realms created.
* For this quickstart we are not going to manually create the realm, but import all configuration from a JSON file. Click on `Select File` and import the [config/quickstart-realm](config/quickstart-realm.json).
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
curl -X POST \
  http://localhost:8180/auth/realms/spring-boot-quickstart/protocol/openid-connect/token \
  -H 'Authorization: Basic YXBwLWF1dGh6LXJlc3Qtc3ByaW5nYm9vdDpzZWNyZXQ=' \
  -H 'content-type: application/x-www-form-urlencoded' \
  -d 'username=alice&password=alice&grant_type=password'
```

After executing the command above, you should get a response similar to the following:

```bash
{
    "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJGSjg2R2NGM2pUYk5MT2NvNE52WmtVQ0lVbWZZQ3FvcXRPUWVNZmJoTmxFIn0.eyJqdGkiOiI3OWY4NmFjZS01Zjk4LTQ0MTctYWJmZC0xMjcyOGQ2OGJkNDEiLCJleHAiOjE1MDQxOTE5MzYsIm5iZiI6MCwiaWF0IjoxNTA0MTkxNjM2LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgxODAvYXV0aC9yZWFsbXMvc3ByaW5nLWJvb3QtcXVpY2tzdGFydCIsImF1ZCI6ImFwcC1hdXRoei1yZXN0LXNwcmluZ2Jvb3QiLCJzdWIiOiJlNmE3NzcyYS1kZmZlLTRiNDItYTFiMS0zZDZmOTM0OWE0NmIiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJhcHAtYXV0aHotcmVzdC1zcHJpbmdib290IiwiYXV0aF90aW1lIjowLCJzZXNzaW9uX3N0YXRlIjoiNzE4MmU0YzEtNzY5ZS00MTNlLWI2MWItM2FlZTFjYWZmY2JmIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgwODAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbInVzZXIiXX0sInJlc291cmNlX2FjY2VzcyI6e30sInByZWZlcnJlZF91c2VybmFtZSI6ImFsaWNlIn0.bpKwmY2CqEm1TLYZoC_6jG0V1XcLKC2dStTAnUJgUMQfTBn3kZHsrWZeahKq7IdVocn7bWoBU0mP8i0rf89GcoZS1j-oju32XArTtE2e-tWVeWaRa1vJHNjhsIAuvZ4CmRh6QOTa-0qowbi1oEZxL3aQ6jPL4OSjBOAJgS51tn4",
    "expires_in":300,
    "refresh_expires_in":1800,
    "refresh_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJGSjg2R2NGM2pUYk5MT2NvNE52WmtVQ0lVbWZZQ3FvcXRPUWVNZmJoTmxFIn0.eyJqdGkiOiI2Mzk3MDRhOS1jYTg1LTQxOWYtODA5Yi03MDkzOGQyNzQwNTQiLCJleHAiOjE1MDQxOTM0MzYsIm5iZiI6MCwiaWF0IjoxNTA0MTkxNjM2LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgxODAvYXV0aC9yZWFsbXMvc3ByaW5nLWJvb3QtcXVpY2tzdGFydCIsImF1ZCI6ImFwcC1hdXRoei1yZXN0LXNwcmluZ2Jvb3QiLCJzdWIiOiJlNmE3NzcyYS1kZmZlLTRiNDItYTFiMS0zZDZmOTM0OWE0NmIiLCJ0eXAiOiJSZWZyZXNoIiwiYXpwIjoiYXBwLWF1dGh6LXJlc3Qtc3ByaW5nYm9vdCIsImF1dGhfdGltZSI6MCwic2Vzc2lvbl9zdGF0ZSI6IjcxODJlNGMxLTc2OWUtNDEzZS1iNjFiLTNhZWUxY2FmZmNiZiIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJ1c2VyIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnt9fQ.CV3tZfYylhXMK7F0ozf-u4AZx_edm0FhdIDYiRDbhM3trRFzmpaRtuwML2KethVqj01PhD0VYYjt2yK0GWgkFswW1tc-Rqq2TMjabeGTcLPwvb8NZ7FcZnwglZUU46mfuV8-m1Idzqgs5DhmpkBALkAXjeaVPedAMNsPFQSPJE4",
    "token_type":"bearer",
    "not-before-policy":0,
    "session_state":"7182e4c1-769e-413e-b61b-3aee1caffcbf"
}
```

Keep the value of ``access_token`` as you will need it later to access resources protected by this application.

Accessing Protected Resources using an OAuth2 Access Token
---------------------

The most simple way to invoke resources protected by a policy enforcer is sending an OAuth2 Access Token. If you successfully obtained an OAuth2 Access Token in the previous section, 
you can access resources in this application as follows:

```bash
curl -v -X GET \
  http://localhost:8080/api/resourcea \
  -H "Authorization: Bearer "$access_token
```

User `alice` should be able to access */api/resourcea* and */api/resourceb* and you should get **Access Granted** as a response.

When using a regular access token to access protected resources, the policy enforcer will query the Keycloak server to check
whether or not the request is allowed to access a resource.

Accessing Protected Resources using Requesting Party Token (RPT)
---------------------

Another approach to access resources protected by a policy enforcer is using a RPT as a bearer token, instead of a regular access token. 
The RPT is an access token with all permissions granted by the server, basically, an access token containing all permissions granted by the server.

To obtain an RPT, you must first exchange an OAuth2 Access Token for a RPT by invoking the token endpoint at the Keycloak server: 

```bash
curl -v -X POST \
  http://localhost:8180/auth/realms/spring-boot-quickstart/protocol/openid-connect/token \
  -H "Authorization: Bearer "$access_token \
  --data "grant_type=urn:ietf:params:oauth:grant-type:uma-ticket" \
  --data "audience=app-authz-rest-springboot" \
  --data "permission=Default Resource"
```

The command above is trying to obtain permissions from the server in the format of a RPT. Note that the request is specifying the resource we want
to obtain permissions, in this case, `Default Resource`.

As an alternative, you can also obtain permissions for any resource protected by your application. For that, execute the command below:

```bash
curl -v -X POST \
  http://localhost:8180/auth/realms/spring-boot-quickstart/protocol/openid-connect/token \
  -H "Authorization: Bearer "$access_token \
  --data "grant_type=urn:ietf:params:oauth:grant-type:uma-ticket" \
  --data "audience=app-authz-rest-springboot"
```

After executing any of the commands above, you should get a response similar to the following:

```bash
{
    "access_token": "${rpt}",
}
``` 

To finally invoke the resource protected by the application, replace the ``${rpt}`` variable below with the value of the ``access_token`` claim from the response above and execute the following command:

```bash
curl -X GET \
  http://localhost:8080/api/resourcea \
  -H "Authorization: Bearer ${rpt}"
```

User `alice` should be able to access */api/resourcea* and you should get **Access Granted** as a response.

Using information from the runtime to evaluate permissions in Keycloak
---------------------

When trying to access the path `/api/admin` from this application, you need to set a specific parameter in the request as follows:

```bash
curl -v -X GET \
  http://localhost:8080/api/admin?parameter-a=claim-value \
  -H "Authorization: Bearer "$access_token
```

If you don't set the request parameter `parameter-a` with that value the request will be denied. The reason for that is that this particular path
is defined as follows in [src/main/resources/application.properties](src/main/resources/application.properties):

```bash
keycloak.policy-enforcer-config.paths[0].path=/api/admin
keycloak.policy-enforcer-config.paths[0].claimInformationPointConfig.claims[some-claim]={request.parameter['parameter-a']}
```

As you can see, we are using a `claim-information-point` definition to set which claims we want to send to Keycloak in order to evaluate permissions
for this particular path. On the Keycloak side there is a specific JavaScript Policy called `Claim-Based Policy` that checks whether or not the `some-claim` is set 
in the evaluation context. 

What to do next ?
----------------------------------

* You can try to access `/api/premium` as user `alice`. The server should deny the request because `alice` is not granted with the required role.
* You can follow the same steps to check behavior when accessing the same resources with user `jdoe`. This user should be allowed to access `/api/premium`.
* Try changing the permissions and policies to check how they affect access to the protected resources.
* Try playing with the `Claim-Based Policy` or create new JavaScript policies to understand how to use the `Policy Evaluation API`.

Integration test of the Quickstart
----------------------------------  

1. Make sure you have an Keycloak server running with an admin user in the `master` realm or use the provided docker image
2. Be sure to set the `TestHelper.keycloakBaseUrl` in the `createArchive` method (default URL is localhost:8180/auth).
3. Set accordingly the correct url for the `keycloak.auth-server-url` in the test [application.properties](src/test/resources/application.properties).
4. Run `mvn test -Pspring-boot`
