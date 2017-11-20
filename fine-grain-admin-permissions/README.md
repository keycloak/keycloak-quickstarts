fine-grain-admin-permissions: Fine Grain Admin Permissions 
===================================================

Level: Intermediate  
Summary: Example realm that illustrates fine grain admin permissions  
Target Product: <span>Keycloak</span>, <span>WildFly</span>  
Source: <https://github.com/keycloak/keycloak-quickstarts>

What is it?
-----------

The `fine-grain-admin-permissions` quickstart demonstrates fine grain administration permissions within a demo realm.
The quickstart initializes a demo realm with admins that have limited administration privileges.
Logging into the demo realm's dedicated admin console as one of these limited admins changes the admin console and what
each of the admins can do.  You will see admins that:

* can only manage one client
* can only view users that are members of a certain group
* can only manage a limited set of role mappings of a specific user

System Requirements
-------------------

The quickstart requires that you have the Keycloak server running.

Configurating the Demo
-----------------------

This quickstart requires that you import an example realm called `fine-grain-demo`.  To do this, log in to the Keycloak
Admin Console at <http://localhost:8080/auth/admin/master/console>.

Prior to running the quickstart you need to create a client in <span>Keycloak</span> and download the installation file.

The following steps show how to import the realm:

* Login into the `master` realm within the <span>Keycloak</span> admin console
* Go to the top left corner where the real menu is and select `Add Realm`
* On the `Add Realm` page, push the `Select file` button within the `Import` field.
* Select the `fine-grain-demo.json` file included within this quickstart
* Click `Create`

Browser the fine-grain-demo Realm
---------------------

Take some time to browser the `fine-grain-demo` realm.

* Go to the `Users` screen and list all the users defined in the group
* Go to the `Clients` screen and view the clients.  There is only one non-built-in client defined `sales-application`
* Go to the `Groups` screen and view the `sales` group.  View the members of this group and notice that not all users in the realm are members of this group.

Managing One Client
----------------------

Open another browser tab and log into `fine-grain-demo` realm's dedicated admin console using the following URL: <http://localhost:8080/auth/admin/fine-grain-demo/console>.
Login as the `sales-admin` user.  The password is `password`.

You will notice that the `sales-admin` user can only manage one client within the `fine-grain-demo` realm's dedicated admin console.

How does this work?  Go back to the old browser tab where you are logged into the `master` realm.  Go to the `sales-application` client
screen and click on the `Permissions` tab.  Within this tab click on the `manage` scope.  This will bring you to the authorization
permission defined for managing this client.  The policy defined for this is a role-based policy.  Any user that has a role mapping
for the client role `sales-application.admin` is able to manage the client.   

Go view the `sales-admin` user in the `Users` screen.  Go to the `Role Mappings` tab for that user and pick the `sales-application` client.
You'll see that this user has the `admin` role mapping for that client.  To be able to view the list of clients he can manage, the
`sales-admin` must also have the `realm-management.query-clients` role.  Pick the `realm-management` client on the `Role Mappings` tab
to see that the sales admin has this role.  

The `sales-admin` inherits the `query-clients` role because the `sales-application.admin` role has a composite
mapping to it.  Go to the `sales-application` client on the `Clients` screen.  Click on `Roles` and navigate to the `admin` role.
You'll see this mapping to `query-clients` here.

Managing Group Members
-----------------------

This section illustrates an admin that can only map certain roles only for users that are members of a specific Group.

Log out of the `fine-grain-demo` realm's dedicated admin console.  Relogin by clicking on this URL:  <http://localhost:8080/auth/admin/fine-grain-demo/console>.
Login as the `sales-manager` user using the password `password`.

You will notice that the `sales-manager` only has the `Users` screen available.  Click on the `View all users` button.  The `sales-manager`
is only allowed to view members of the `sales` group.  Click on `Joe`. The `sales-manager` has mostly only view permissions for `Joe`.

The `sales-manager` is able to manage some role mappings for `Joe`.  Click on `Joe`'s `Role Mappings` tab.  Browse admin and all client roles.
You'll see that the `sales-manager` can only add roles from the `sales-application` client.  Add the `admin` role for `Joe`.  Now,
Joe can log into the admin console and manage the `sales-application` client. 

How does this work?  Let's first examine how the `sales-manager` is only able to view users of a certain Group.  
Go back to the old browser tab where you are logged into the `master` realm. Go to the `Groups` screen and select the `sales` group.
Go to the `Permissions` tab and click the `view-members` scope.  This will bring you to `view-members` permission in the Authorization
Service.  You will see that there is a policy there attached to the permission.  If you navigate to this policy you'll see that it is a
User Policy that resolves only to the `sales-manager`.  This permission and policy setup allows the `sales-manager` to view all users
that are members of the `sales` group.  One last thing required is that the `sales-manager` must have the `realm-management.query-users` client
role mapping.  If you go to the `Role Mappings` tab for the `sales manager`, you'll see this role mapping.

The setup above is only for view group members.  To see how the `sales-manager` is able to map a limited set of role mappings, there's other areas
we have to investigate.  Go to the `Users` screen where you can list all users.  Click on the `Permissions` tab.  On this screen
click the `map-roles` scope link.  This permission defines who is allowed to perform user role mappings.  You'll see that there is a policy
defined for this permission that points to the `sales-manager`.  

Still not done yet...  The `Users` `map-roles` permission defines who can map roles, but no which roles the admin can apply.  This must be
defined individual for each role.  This would be very time consuming so there is a short cut for giving permission for all the roles defined by 
a client.  Go to the `sales-applicaiton` client's page.  Click on the `Permissions` tab.  Click the `map-roles` scope.  You'll see that
this permission has a User Policy attached to it that links in the `sales-manager`.
  