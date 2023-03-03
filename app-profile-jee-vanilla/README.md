## app-profile-jee-vanilla: JSP Profile Application

Level: Beginner  
Technologies: JavaEE  
Summary: JSP Profile Application with Basic Authentication  
Target Product: <span>Keycloak</span>, <span>WildFly</span>  
Source: <https://github.com/keycloak/keycloak-quickstarts>

### What is it?

The `app-profile-jee-vanilla` quickstart demonstrates how to change a JavaEE application that is secured with basic authentication without any changes to the WAR itself. Changing the authentication method and injecting the configuration is done automatically by the <span>Keycloak</span> client adapter subsystem.

### System Requirements

See the [Getting Started Guide](../docs/getting-started.md) for the minimum requirements and steps to build and run the quickstart.

### Build and Deploy the Quickstart

Unlike most other quickstarts, for this quickstart you should first deploy the application to display that it's secured with basic authentication. Afterwards, you will configure the client adapter subsystem to secure the application and re-deploy the application to see the application is now secured with <span>Keycloak</span> without having to do any changes to the application itself.

1. Open a terminal and navigate to the root directory of this quickstart.
2. Run the following command to deploy the quickstart:

   ````
   mvn clean wildfly:deploy
   ````


Access the Quickstart
----------------------

You can access the application with the following URL: <http://localhost:8080/vanilla>. If you click on the
login button the browser will display a prompt for authentication required. This is used for basic authentication where
a username and password is collected by the browser and sent to the web application with the authorization header.

At the moment you are not able to authenticate unless you have configured your <span>WildFly</span> server with a realm and users
for basic authentication.

The next step is to configure the <span>Keycloak</span> client adapter subsystem to configure the application to use <span>Keycloak</span> for
authentication instead.


## Configure Client Adapter Subsystem

Before configuring the adapter subsystem, you need to create a client in <span>Keycloak</span>. Follow these steps to create a client and configure it for your application:

### Step 1: Create a Client

1. Navigate to the Keycloak admin console and click on the `Clients` tab. Click the `Create` button to create a new client.
2. Set the `Client Type` to `OpenID Connect`.
3. Enter a `Client ID` for your client (for example `app-profile-vanilla`).
4. Enter a `Name` for your client (for example `Vanilla App Profile`).
5. Click `Next`.
6. Set `Client Authentication` to `On`. Leave all other options on their default values.
7. Click `Save`.

### Step 2: Configure the Client

Once you have created your client, you need to configure it for your application. Follow these steps:

1. Click on your client in the Keycloak admin console.
2. Set the `Root URL` of your application to `http://localhost:8080/vanilla`.
3. Set the `Valid Redirect URIs` field to `http://localhost:8080/vanilla/*`.
4. Click `Save`.

That's it! You have now created a client and configured it for your application.

If you deploy the application somewhere else change the hostname and port of the URLs accordingly.

### Configuring the OIDC Adapter with Keycloak

To secure your application with Keycloak, you need to configure the OIDC adapter via the Keycloak client adapter subsystem. Follow these steps:

1. Log in to the Keycloak admin console and click on the client you created.
2. Click on the `Installation` tab and select `Keycloak OIDC JSON`.
3. Copy the JSON snippet to the clipboard.
4. Open the `oidc.json` file located in `src/webapp/WEB-INF/` of your project in an editor.
5. Paste the JSON code you copied from Keycloak into `oidc.json`.
6. Save the `oidc.json` file.

---

## Redeploying the Application

Once you have configured the OIDC adapter, you need to redeploy the application. To do this, follow these steps:

1. From the root of the quickstart redeploy the application using the below command.
   ````
   mvn wildfly:redeploy
   ````
2. Once the application is redeployed, access it at `http://localhost:8080/vanilla`.
3. Try to log in again. This time, you will be redirected to Keycloak to authenticate.

That's it! With these changes made, your application should now be secured with Keycloak. Note that the `auth-method` of the `web.xml` file is set to `oidc` by default and is already configured to work with the OIDC adapter, so you don't need to set it manually.


Undeploy the Quickstart
--------------------

1. Open a terminal and navigate to the root of the <span>Keycloak</span> server directory.

2. The following shows the command to undeploy the quickstart:

   ````
   mvn wildfly:undeploy
   ````
