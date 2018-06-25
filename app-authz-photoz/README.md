app-authz-uma-photoz: HTML5 + AngularJS client application that interacts with a protected RESTFul API
================================================

Level: Beginner  
Technologies: JAX-RS, HTML5, AngularJS  
Summary: AngularJS client application that accesses a protected RESTFul API based on JAX-RS  
Target Product: <span>Keycloak</span>, <span>WildFly</span>
Source: <https://github.com/keycloak/keycloak-quickstarts>  

What is it?
-----------

The `app-authz-photoz` quickstart demonstrates how Keycloak Authorization Services can be used to protect
access to resources (in this case photo albums and user profiles). It consists of a simple application based on *HTML5*
+ *AngularJS* + *JAX-RS* that will introduce the reader to some of the main concepts around Keycloak Authorization Services.

Basically, it is a project containing three modules:
 
* **photoz-restful-api**, a simple RESTFul API based on JAX-RS and acting as a resource server.
* **photoz-html5-client**, a HTML5 + AngularJS client that will consume the RESTful API published by a resource server.
* **photoz-authz-policy**, a simple project with some rule-based policies using JBoss Drools.

For this application, users can be regular users or administrators. Regular users can create/view/delete their albums 
and administrators can do anything. Regular users are also allowed to share their albums with other users.

In Keycloak, albums and the user profile are resources that must be protected based on a set of policies that defines the
access constraints.

The resources are also associated with a set of scopes that defines a specific access context. In this case, resources have
the following scopes:

* album:view
* album:delete
* profile:view

The authorization requirements for this quickstart are based on the following assumptions:

* By default, any regular user can perform any operation on his resources.

    * For instance, Alice can create, view and delete her own albums. 

* Authorization for viewing and deleting albums can be granted by the owner to other users.

    * The owner can share specific albums with other users and define the scopes associated with each shared album.

* Administrators can view albums from all users (via Administration API) and can also delete any album.

* Regular users can only view their own profiles. The profile contains information such as the user id and number of albums 
  created.

* Administrators are only authorized to access resources if the client's ip address is well known

That said, this quickstart will show you how to use the Keycloak to define policies using:

* Role-based Access Control
* Attribute-based Access Control
* Rule-based policies using JBoss Drools
* Rule-based policies using JavaScript 

This quickstart demonstrates how to allow users to manage access
to their resources using the *Keycloak Account Service* and UMA. It also shows how to create resources dynamically and how to
protect them using the *Protection API* and the *Authorization Client API*. Here you'll see how to create a resource whose
owner is the authenticated user.

In addition it provides some background on how one can actually protect JAX-RS endpoints using a *policy enforcer*.

Differently than the quickstart `app-authz-uma-photoz`, this quickstart demonstrates how to access protect resources using
regular access tokens instead of RPTs. The `photoz-html5-client` application does not perform any additional step to
obtain RPTs from the Keycloak server but just use the access token obtained during the authentication as a bearer token
when accessing protected resources.

Create the Example Realm and a Resource Server
-----------

Considering that your Keycloak Server is up and running, log in to the Keycloak Administration Console.

Now, create a new realm based on the following configuration file:

   ````
   keycloak-quickstarts/app-authz-photoz/photoz-realm.json
   ````
    
That will import a pre-configured realm with everything you need to run this quickstart. For more details about how to import a realm 
into Keycloak, check the Keycloak's reference documentation.

After importing that file, you'll have a new realm called `photoz`. 

Back to the command-line, build the quickstart. This step is necessary given that we're using policies based on
JBoss Drools, which require `photoz-authz-policy` artifact installed into your local maven repository.

To build the quickstart, open a terminal and navigate to the root of this quickstart. Then run the following command:

   ````
   mvn clean install 
   ````
    
Now, let's import another configuration using the Administration Console in order to configure the client application ``photoz-restful-api`` as a resource server with all resources, scopes, permissions and policies.

Click on `Clients` on the left side menu. Click on the `photoz-restful-api` on the client listing page. This will
open the `Client Details` page. Once there, click on the `Authorization` tab. 

Click on the `Select file` button, which means you want to import a resource server configuration. Now select the file that is located at:

    keycloak-quickstarts/app-authz-photoz/photoz-restful-api/target/classes/photoz-restful-api-authz-service.json
    
Now click `Upload` and the resource server will be updated accordingly.

Deploy and Run the quickstart applications
-----------

To deploy the quickstart applications, first deploy the client:

1. Open a terminal and navigate to the root directory of this quickstart.

2. Enter the `photoz-html5-client` module:

   ````
   cd photoz-html5-client
   ````

3. Deploy the client application:

   ````
   mvn clean wildfly:deploy
   ````

The next step is to the deploy the `photoz-restful-api` application:

1. Go back to the root directory of this quickstart.

2. Enter the `photoz-restful-api` module:

   ````
   cd photoz-restful-api
   ````
   
3. Deploy the RESTFul application:

   ````
   mvn clean package wildfly:deploy
   ```` 

Now, try to access the client application using the following URL:

    http://localhost:8080/photoz-html5-client

If everything is correct, you will be redirect to Keycloak login page. You can login to the application with the following credentials:

* username: jdoe / password: jdoe
* username: alice / password: alice
* username: admin / password: admin

Creating and Sharing Resources
----------

Lest's first login as Alice (username: alice, password: alice). Once logged in, the `photoz` client app will display a simple
page containing some links that can be used to display things like the Access Token. While it is interesting to
view the token contents, we will focus on the main functionality of the quickstart, which is managing albums.

* Before creating any albums, click on the `My Profile` link. A page will display Alice's id and also the number of albums
owned by Alice, which should be zero at this point.
* The next step will be creating a couple of albums. Click on the `Create Album` link to add an album. For example purposes,
let's crete two albums, named `Italy Vacations` and `Greece Vacations`.
* Clicking on the `My Profile` link now should show a total of two albums owned by Alice.
* It is also possible to delete an album by clicking on the `[X]` link next to it but we won't do it for the moment.

Alice can now manage her albums and share them with other users. Click on the `My Account` link at the top of the page. You
will be directed to the Keycloak Account page. Next, click on `My Resources`. It will show the two albums created before in
a table. Let's now share the albums with John Doe (jdoe):

* Click on the `Italy Vacations` album. In the `Share with others` section fill the input field with the `jdoe` username
and click on the `Share` button. By default both the `album:view` and `album:delete` scopes are set as permissions for
John. 
* The table at the top of the page now displays information about the shared album: the username with which the album was
shared with, the permissions (scopes) this username has on the resource, the time and date of the permission grant and a
`Revoke` buttom that Alice can use at any time to revoke access to her album.
* Click again on the `My Resources` link in the left panel. Notice how the table of resources now has a shared symbol
in the `Italy Vacations` album along with the number `1`, indicating the album has been shared with 1 other user.
* Now click on the `Greece Vacations` album, enter `jdoe` in the input field but before clicking `Share` make sure to remove
the `album:delete` scope.
* The table at the top of the page again displays information about the shared resource. Notice how only the `album:view`
scope has been set in the `Permission` column.

So, to summarize, Alice has shared both her albums with John, giving him full permissions on the `Italy Vacations` album
and only the `album:view` permission on the `Greece Vacation` album.

Now click on the `Back to photoz-html5-client` link at the top of the page to return to the application and then click on
the `Sign Out` link to logout.

Viewing and Interacting with Shared Resources
----------

Now let's login as John (username: jdoe, password: jdoe). The main page now shows that John doesn't own any albums yet but
he has albums that were shared with him. Clicking on the `My Profile` link will diplay John's id and zero albums.

The list of albums shared with John displays two entries. John should be allowed to remove `Italy Vacations` but he should not be able to
delete `Greece Vacation`. That is because Alice previously granted to john only scope `album:view` in this album. 

Viewing All Resources
---------

One of the requirements of the application is that administrators should be able to view and delete albums from all users,
so let's test this:

* If you are logged in as John or Alice, hit the `Sign out` link to logout.
* Sign in as the administrator (username: admin, password: admin). You will notice the main page is a little bit different
from the regular user one. An admin has link named `All Albums`. Click on this link. A list of all albums created so far
is displayed by user.
* As a final exercise as the administrator, remove the `Greece Vacations` album. Then logout and log back in as both
John and Alice to see how the album has been excluded.

Revoking Permissions
---------

As a final exercise let's say that Alice now doesn't trust John anymore so she wants to revoke the permissions she gave him.
Log in as Alice (username: alice, password: alice) and go to `My Account` and then click on `My Resources`. She has now only
one album (`Italy Vacations`) as we removed the other one as administrators. Click on the album and now the page shows the
table of people with access to the resource. Click on the `Revoke` button to revoke the permissions granted to John.

Click on the `Back to photoz-html5-client` link and the logout. Log back in as John (username: jdoe, password: jdoe) and
notice how John has not albums shared with him.

Summary
----------

This quickstart should provides a good overview of some of the core concepts of the Keycloak Authorization Services, such as
user-managed access (privacy control) and resource sharing.