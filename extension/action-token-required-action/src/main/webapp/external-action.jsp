<%@page import="java.net.URLEncoder"%>

<html>
<head>
<title>Keycloak Sample External Application</title>
<meta>
</meta>
</head>

<body>
    After submit, you will be redirected back to Keycloak as authenticated user. Then as an administrator, open the corresponding user in the Keycloak admin console
    and see in the "Attributes" tab in the admin console that he has two attributes set on himself based on what you just filled.
    <form action="submit-back.jsp" accept-charset="UTF-8">
        <input name="_tokenUrl" type="hidden" value="<%= request.getParameter("token") %>">
        Field 1: <input name="field_1">
        <br>
        Field 2: <input name="field_2">
        <br>
        <button type="submit">Submit value back to Keycloak</button>
    </form>
</body>
</html>
