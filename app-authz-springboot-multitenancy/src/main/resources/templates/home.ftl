<#import "/spring.ftl" as spring />
<#assign xhtmlCompliant = true in spring>
<!DOCTYPE html>
<html>
<head>
    <title>Home Page</title>
</head>
<body>

<header>
     <h2>Welcome, ${identity.name}</h2>
     <h3>You are accessing the application within tenant <b>${identity.tenant} boundaries</b></h3>

     <p><a href="/${identity.tenant}/logout" id="logout">Logout</a></p>
    <p><select onchange="document.location = '/' + this.value" id="tenant-select"><option>Change Tenant...</option><option value="realm-a">Tenant A</option><option value="realm-b">Tenant B</option></select></p>
     <p></p>

     <p><a id="protected-resource" href="/${identity.tenant}/protected">Any authenticated user with a role "user" can access this resource</a></p>
     <p><a id="premium-resource" href="/${identity.tenant}/protected/premium">Only users with a role "user-premium" can access this resource</a></p>

     <#if identity.hasResourcePermission('Alice Resource')>
        <p><a id="alice-resource" href="/${identity.tenant}/protected/alice">Only user "alice" can access this resource</a></p>
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