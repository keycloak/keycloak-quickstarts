app-authz-uma-photoz: HTML5 + AngularJS client application that interacts with a protected RESTFul API
================================================

Level: Intermediate  
Technologies: JAX-RS, HTML5, AngularJS  
Summary: AngularJS client application that accesses a protected RESTFul API based on JAX-RS  
Target Product: <span>Keycloak</span>, <span>WildFly</span>
Source: <https://github.com/keycloak/keycloak-quickstarts>  

What is it?
-----------

The `app-authz-uma-photoz` quickstart demonstrates how User-Managed Access (UMA 2.0) can be used in Keycloak to manage
access to resources (in this case photo albums and user profiles). It consists of a simple application based on *HTML5*
+ *AngularJS* + *JAX-RS* that will introduce the reader to some of the main concepts around Keycloak Authorization Services.

Basically, it is a project containing three modules:
 
* **photoz-restful-api**, a simple RESTFul API based on JAX-RS and acting as a resource server.
* **photoz-html5-client**, a HTML5 + AngularJS client that will consume the RESTful API published by a resource server.
* **photoz-js-policies**, a simple project with some rule-based policies using JavaScript.

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
* Rule-based policies using JavaScript 

This quickstart demonstrates how to enable User-Managed Access (UMA) in an application in order to allow users to manage access
to their resources using the *Keycloak Account Service*. It also shows how to create resources dynamically and how to
protecte them using the *Protection API* and the *Authorization Client API*. Here you'll see how to create a resource whose
owner is the authenticated user.

In addition it provides some background on how one can actually protect JAX-RS endpoints using a *policy enforcer*.

Create the Example Realm and a Resource Server
-----------

Build the quickstart by running the following command:

   ````
   mvn clean install 
   ````

This quickstart defines some policies using JavaScript. Before creating the realm, you need to deploy these policies to the Keycloak Server as follows:

1. Open a terminal and navigate to the root directory of this quickstart.

2. Enter the `photoz-js-policies` module:

   ````
   cd photoz-js-policies
   ````

3. Deploy the policies to the server as follows:

   ````
   cp target/photoz-uma-js-policies.jar KEYCLOAK_HOME/standalone/deployments
   ````

Now, log in to the Keycloak Administration Console and create a new realm based on the following configuration file:

   ````
   keycloak-quickstarts/app-authz-uma-photoz/photoz-realm.json
   ````
    
That will import a pre-configured realm with everything you need to run this quickstart. For more details about how to import a realm 
into Keycloak, check the Keycloak's reference documentation.

After importing that file, you'll have a new realm called `photoz`.

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

Let's first login as Alice (username: alice, password: alice). Once logged in, the `photoz` client app will display a simple
page containing some links that can be used to display things like the RPT or the Access Token. While it is interesting to
view the token contents, we will focus on the main functionality of the quickstart, which is managing albums.

* Before creating any albums, click on the `My Profile` link. A page will display Alice's id and also the number of albums
owned by Alice, which should be zero at this point.
* The next step will be creating a couple of albums. Click on the `Create Album` link to add an album. For example purposes,
let's craete two albums, named `Italy Vacations` and `Greece Vacations`.
* Clicking on the `My Profile` link now should show a total of two albums owned by Alice.
* It is also possible to delete an album by clicking on the `[X]` link next to it but we won't do it for the moment.

Alice can now manage her albums and share them with other users. Click on the `My Account` link at the top of the page. You
will be directed to the Keycloak Account page. Next, click on `Resources`. It will show the two albums created before in
a table. Let's now share the albums with John Doe (jdoe):

* Click on the `Share` button for the `Italy Vacations` album. In the `Username or email` field enter the `jdoe` username
  and click on the `Add` button. Click on the `Select the permissions` field and select both `album:view` and `album:delete`
  permissions from the drop-down menu. Click `Done` to confirm the changes.
* Alice can use the kebab menu for the `Italy Vacations` album at any time to view and change the permissions
  or revoke the access completely.
* Click the `ˬ` (arrow down) button next to `Italy Vacations` resource name to expand the box. Notice the information that
  the resource is shared with `jdoe`.
* Now, click on the `Share` button for the `Greece Vacations` album and enter `jdoe` username again. This time, select
  only the `album:view` permission and confirm the dialog by clicking on the `Done` button.

So, to summarize, Alice has shared both her albums with John, giving him full permissions (`album:view` and `album:delete`)
on the `Italy Vacations` album and only the `album:view` permission on the `Greece Vacation` album.

Now click on the `Back to photoz-html5-client` link at the top of the page to return to the application and then click on
the `Sign Out` link to logout.

Viewing and Interacting with Shared Resources
----------

Now let's login as John (username: jdoe, password: jdoe). The main page now shows that John doesn't own any albums yet but
he has albums that were shared with him. Clicking on the `My Profile` link will diplay John's id and zero albums.

The list of albums shared with John displays two entries but notice how only the `Italy Vacations` album has the `[X]` link
that allows for the deletion of the album. For the `Greece Vacations` albums we have a different link: `Request Delete Access`.
This tells John that he doesn't have the permissions to delete this album and if he wants to do so he needs to ask Alice
(the owner) for the permission to do so. Let's do some things as John:

* First create an album. Let's call it `Germany Vacations`. We won't be sharing this album with anybody but the `admin`
should still be able to see it when he logs (we will cover that part later on) because administrators should be able to
see all the albums created by all users.
* The next step is to request Alice the permissions to delete the `Greece Vacations` album. Click on the `Request Delete Access`
link. The app displays the following message:
   ````
   Sent authorization request to resource owner, please, wait for approval.
   ````
* Next click on the `My Account` link at the top of the page and then click on `Resources`. Notice how the page shows
both John's own resources (the `Germany Vacations`) album and also the resources Alice shared with him (on `Shared with Me`
tab).
* We won't be doing anything for now on this page, so let's just logout. For that, simply click on the `Sign Out` link at
the top of the page.

Managing Permission Requests
----------

We know now that John has sent Alice a request to obtain `album:delete` for the `Greece Vacations` album. So let's log back
in as Alice (username: alice, password: alice). We should be take straight to the Keycloak Account page. If you land at the
application main page click on the `My Account` link to go to the Keycloak Account page.

Click on the `Resources` link and now the page looks a little bit different. A button with `1` badge is displayed next to
the `Greece Vacations` resource. After clicking that button a modal dialog appears. It shows that John has requested
the `album:delete` permission for the album and it also displays two buttons, `Accept` and `Deny`. At this moment we will
approve John's request, so click on the `Accept` button.

Click on the `Back to photoz-html5-client` link at the top of the page and then logout by clicking on the `Sign out` link.

Log back in as John (username: jdoe, password: jdoe) and notice how the `Greece Vacations` album now has the `[X]` link active
next to it, meaning that John now has the permission to delete this album.

Viewing All Resources
---------

One of the requirements of the application is that administrators should be able to view and delete albums from all users,
so let's test this:

* If you are logged in as John or Alice, hit the `Sign out` link to logout.
* Sign in as the administrator (username: admin, password: admin). You will notice the main page is a little bit different
from the regular user one. An admin has link named `All Albums`. Click on this link. A list of all albums created so far
is displayed by user. Notice how the `Germany Vacations` album is linked to the John's id while the other two albums are
linked to Alice's id (visible in the `My Profile` page for both users).
* As a final exercise as the administrator, remove the `Greece Vacations` album. Then logout and log back in as both
John and Alice to see how the album has been excluded.

Revoking Permissions
---------

As a final exercise let's say that Alice now doesn't trust John anymore so she wants to revoke the permissions she gave him.
Log in as Alice (username: alice, password: alice) and go to `My Account` and then click on `Resources`. She has now only
one album (`Italy Vacations`) as we removed the other one as administrators. Select `Edit` from the kebab menu for this
resource, remove all permissions from the `Select the permissions` field and confirm the changes by clicking the "check"
button next to that field. Finally, close the modal dialog by clicking `Done`. Alternatively, select `Unshare all` from
the kebab menu which would revoke access from all users Alice shared the album with.

Click on the `Back to photoz-html5-client` link and the logout. Log back in as John (username: jdoe, password: jdoe) and
notice how John has not albums shared with him.

Summary
----------

This quickstart should provide a good overview of some of the core concepts of the Keycloak Authorization Services, such as
user-managed access (privacy control), resource sharing, and asynchronous authorization.

Integration test of the Quickstart
----------------------------------

1. Make sure you have an Keycloak server running with an admin user in the `master` realm or use the provided docker image.
2. Run `mvn install -Pwildfly-managed`. Add `-Dbrowser=chrome` to run the tests without the headless browser mode.