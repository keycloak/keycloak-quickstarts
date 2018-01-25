user-storage-jpa: User Storage Provider with EJB and JPA
========================================================

Level: Beginner  
Technologies: JavaEE, EJB, JPA  
Summary: User Storage Provider with EJB and JPA  
Target Product: <span>Keycloak</span>  
Source: <https://github.com/keycloak/keycloak-quickstarts>  


What is it?
-----------

This is an example of the User Storage SPI implemented using EJB and JPA.  It shows you how you might use these components
to integrate <span>Keycloak</span> with an existing external custom user database.  The example integrates with a simple relational
database schema that has one user table that stores a username, email, phone number, and password for one particular user.
Using the User Storage SPI this table is mapped to the <span>Keycloak</span> user metamodel so that it can be consumed by the <span>Keycloak</span>
runtime. Before using this example, you should probably read the User Storage SPI chapter of our server developer guide.


System Requirements
-------------------

You need to have <span>Keycloak</span> running.

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later.


Build and Deploy the Quickstart
-------------------------------

You must first deploy the datasource it uses.
Start up the <span>Keycloak</span> server.  Then in the directory of this example type the following maven command:

   ````
   mvn -Padd-datasource install
   ````

You only need to execute this maven command once.  If you execute this again, then you will get an error message that the datasource
already exists.

If you open the pom.xml file you'll see that the add-datasource profile creates an XA datasource using the built
in H2 database that comes with the server.  An XA datasource is required because you cannot use two non-xa datasources
in the same transaction.  The <span>Keycloak</span> database is non-xa.

Another thing to note is that the xa-datasource created is in-memory only.  If you reboot the server, any users you've
added or changes you've made to users loaded by this provider will be wiped clean.

To deploy the provider, run the following maven command:

    ````
    mvn clean install wildfly:deploy
    ````

If you want to play with and modify the example, simply rerun the maven deploy command above and the new version will be hot deployed.

Enable the Provider for a Realm
-------------------------------
Login to the <span>Keycloak</span> Admin Console and got to the User Federation tab.   You should now see your deployed provider in the add-provider list box.
Add the provider, save it.  This will now enable the provider for the 'master' realm.  Because this provider implements the UserRegistrationProvider interface, any new user you create in the
admin console or on the registration pages of <span>Keycloak</span>, will be created in the custom store used by the provider.  If you go
to the Users tab in the Admin Console and create a new user, you'll be able to see the provider in action.

Integration test of the Quickstart
----------------------------------

1. Make sure you have an Keycloak server running with an admin user in the `master` realm or use the provided docker image
2. Run `mvn test -Pkeycloak-remote`

More Information
----------------
The User Storage SPI and how you can use Java EE to implement it is covered in detail in our server developer guide.

