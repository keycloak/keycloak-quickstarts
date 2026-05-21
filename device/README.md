Spring Boot Application Demonstrating Keycloak Device Authorization Grant
===================================================

Level: Beginner
Technologies: Spring Boot, Thymeleaf
Summary: Spring Boot Application Demonstrating Keycloak Device Authorization Grant (OAuth 2.0 Device Flow)
Target Product: Keycloak

What is it?
-----------

This quickstart demonstrates how to implement the OAuth 2.0 Device Authorization Grant (Device Flow) using Keycloak and Spring Boot.

The Device Flow is designed for devices that either lack a browser or have limited input capabilities. It allows users to authenticate and authorize applications on such devices by using a secondary device (like a smartphone or computer) with a browser.

This quickstart includes:
* A Spring Boot application that initiates the device flow
* Automatic polling for authorization status
* A web interface to display device codes and verification URLs
* Integration with Keycloak's device authorization endpoint

System Requirements
-------------------

To compile and run this quickstart you will need:

* JDK 17
* Apache Maven 3.8.6
* Spring Boot 3.2.4
* Keycloak 21+
* Docker 20+

Starting and Configuring the Keycloak Server
-------------------

To start a Keycloak Server you can use OpenJDK on Bare Metal, Docker, Openshift or any other option described in [Keycloak Getting Started guides](https://www.keycloak.org/guides#getting-started). For example when using Docker just run the following command in the root directory of this quickstart:

```shell
docker run --name keycloak \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  -p 8080:8080 \
  quay.io/keycloak/keycloak:{KC_VERSION} \
  start-dev
```

where `KC_VERSION` should be set to 21.0.0 or higher.

You should be able to access your Keycloak Server at http://localhost:8080.

Log in as the admin user to access the Keycloak Administration Console. Username should be `admin` and password `admin`.

Import the [realm configuration file](config/realm-import.json) to create a new realm called `device-flow-quickstart`.
For more details, see the Keycloak documentation about how to [create a new realm](https://www.keycloak.org/docs/latest/server_admin/index.html#_create-realm).

The realm includes:
* A pre-configured user `alice` with password `password`
* A public client `device-client` with device authorization grant enabled
* Device code lifespan set to 600 seconds (10 minutes)
* Polling interval set to 5 seconds

Build and Run the Quickstart
-------------------------------

If this is the first time you're running the quickstart application, you will need to install the parent POM in your local Maven repository. From the root of the repository, do the following:

```
mvn clean install
```

If your Keycloak server is up and running, perform the following steps to start the application:

1. Open a terminal and navigate to the root directory of this quickstart.

2. The following shows the command to run the application:

   ````
   mvn spring-boot:run
   ````

3. The application will start on port 8081 (to avoid conflicts with Keycloak running on port 8080).

Access the Quickstart
---------------------

Once the application is running, you can access it at:

* http://localhost:8081/

The application provides a simple web interface where you can:

1. **Initiate Device Flow**: Click the button to start the device authorization flow:
   - The application sends a request to the Keycloak server's device authorization endpoint requesting a device code and user code
   - The server responds with the codes needed for the authorization process
2. **View Device Code**: The application will display:
   - A user code that needs to be entered on the verification page
   - A verification URI where the user should navigate
   - A complete verification URI with the code pre-filled
3. **Authorize on Secondary Device**: 
   - Open the verification URI in a browser (can be on a different device)
   - Log in with username `alice` and password `password`
   - Enter the user code when prompted (or use the complete verification URI)
   - Give consent and approve the authorization request
4. **Automatic Token Retrieval**: The application automatically polls Keycloak every 5 seconds and will display the access token once authorization is granted

Understanding the Device Flow
---------------------

The OAuth 2.0 Device Authorization Grant flow works as follows:

1. **Device Authorization Request**: The application requests a device code from Keycloak
   ```
   POST /realms/device-flow-quickstart/protocol/openid-connect/auth/device
   client_id=device-client
   scope=openid profile
   ```

2. **Device Authorization Response**: Keycloak returns:
   - `device_code`: Used by the application to poll for authorization
   - `user_code`: Displayed to the user for manual entry
   - `verification_uri`: URL where the user authorizes the device
   - `verification_uri_complete`: URL with the user code pre-filled
   - `expires_in`: How long the codes are valid
   - `interval`: Recommended polling interval

3. **User Authorization**: The user navigates to the verification URI on a secondary device, logs in, and authorizes the application

4. **Token Polling**: The application polls the token endpoint:
   ```
   POST /realms/device-flow-quickstart/protocol/openid-connect/token
   grant_type=urn:ietf:params:oauth:grant-type:device_code
   client_id=device-client
   device_code={device_code}
   ```

5. **Token Response**: Once authorized, Keycloak returns an access token

Polling Behavior
---------------------

The application implements automatic polling with the following behavior:

* Polls every 5 seconds (as configured in the realm)
* Handles various response codes:
  - `authorization_pending`: Continues polling
  - `slow_down`: Logs a message and continues polling
  - `access_denied`: Stops polling and displays error
  - `expired_token`: Stops polling and displays error
* Stops polling once an access token is received

Running Tests
--------------------

Make sure Keycloak is [running](#starting-and-configuring-the-keycloak-server).

1. Open a terminal and navigate to the root directory of this quickstart.

2. Run the following command to build and run tests:

   ````
   mvn clean verify
   ````

The tests include:
* Unit tests for the DeviceFlowService
* Integration tests for the complete device flow

References
--------------------

* [OAuth 2.0 Device Authorization Grant (RFC 8628)](https://datatracker.ietf.org/doc/html/rfc8628)
* [Keycloak OAuth 2.0 Device Authorization Grant](https://www.keycloak.org/docs/latest/securing_apps/#_device-authorization-grant)
* [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
* [Keycloak Documentation](https://www.keycloak.org/documentation)
