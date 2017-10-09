user-storage-properties: User Storage SPI Simple Example
========================================================

Level: Beginner  
Technologies: JavaEE  
Summary: User Storage SPI Simple Example  
Target Product: <span>Keycloak</span>  
Source: <https://github.com/keycloak/keycloak-quickstarts>  


What is it?
-----------

This quickstart implements two user storage providers using the User Storage SPI.  These providers
are backed by a simple properties file that contains username/password key pairs.  Once you deploy and enable these providers
for a specific realm, you will be able to login to that realm using the users defined in these properties files.  Before
using this example, you should probably read the User Storage SPI chapter of our server developer guide.

The `readonly-property-file` provider is hardcoded to look within the `users.properties` file embeded in the deployment jar
of the providers.  This property file, `users.properties` is in the `resources` directory of this project.
There is one user 'tbrady' with a password of 'superbowl'.  You will only be able to add new users to this provider
by editing the properties file and redeploying the provider.  Users loaded by this provider are read-only and cannot be modified
at all.

The `writeable-property-file` provider must be configured to point to a property file on disk when you enable it for a realm.
Users loaded from this property file are not read only.  Only username and password are stored in the properties file.  Any additional
attributes you add to the user are stored in federated storage.  This provider also supports adding users at runtime, so you can
add new users in the admin console and they will be created within the property file on disk .


System Requirements
-------------------

You need to have <span>Keycloak</span> running.

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later.


Build and Deploy the Quickstart
-------------------------------

To deploy this provider you must have <span>Keycloak</span> running in standalone or standalone-ha mode. Then type the follow maven command:

   ````
   mvn clean install wildfly:deploy
   ````
If you want to play with and modify the example, simply rerun the maven deploy command above and the new version will be hot deployed.

Enable the Provider for a Realm
-------------------------------
Login to the <span>Keycloak</span> Admin Console and got to the User Federation tab.   You should now see your deployed providers in the add-provider list box.
For the `readonly-property-file` provider, all you need to do is add the provider and save it as it is hardcoded to point
to the property file that comes with the deployment.  You will be able to login to the account service using the username `tbrady` and password
of `superbowl`.

For the `writeable-property-file` provider, you will have to specify a properties file on disk on the configuration page of the provider.
This file can be empty, but it must exist on disk otherwise the provider will fail.
Because this provider implements the UserRegistrationProvider interface, any new user you create in the
admin console or on the registration pages of <span>Keycloak</span>, will be created in the properties file you configured.  If you go
to the Users tab in the Admin Console and create a new user, you'll be able to see the provider in action.  You can also
edit the file yourself to add the username/password pairs you want.

Integration test of the Quickstart
----------------------------------

1. Make sure you have an Keycloak server running with an admin user in the `master` realm or use the provided docker image
2. Run `mvn test -Pkeycloak-remote`

More Information
----------------
This particular example is discussed in detail in the User Storage SPI chapter of the server developer guide.  Each line of code is dissected
to show you how the User Storage SPI works.
