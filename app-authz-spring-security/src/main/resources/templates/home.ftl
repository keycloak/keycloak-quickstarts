<#import "/spring.ftl" as spring />
<#assign xhtmlCompliant = true in spring>
<!DOCTYPE html>
<html>
<head>
    <title>Home Page</title>
</head>
<body>

<header>
     <h3>Welcome, ${identity.name}</h3>

     <p><a href="/logout" id="logout">Logout</a></p>
     <p></p>

     <p><a id="protected-resource" href="/protected">Any authenticated user with a role "user" can access this resource</a></p>
     <p><a id="premium-resource" href="/protected/premium">Only users with a role "user-premium" can access this resource</a></p>

     <#if identity.hasResourcePermission('Alice Resource')>
        <p><a id="alice-resource" href="/protected/alice">Only user "alice" can access this resource</a></p>
     </#if>

     <p>You have permissions to access the following resources:</p>

     <ul>
         <#list identity.permissions as permission>
             <li>
                <p>${permission.resourceName}</p>
            </li>
         </#list>
     </ul>
</header>


</body>
</html>