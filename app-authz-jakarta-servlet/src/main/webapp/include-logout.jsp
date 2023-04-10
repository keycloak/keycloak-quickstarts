<jsp:useBean id="controller" class="org.keycloak.quickstart.Controller" scope="request"/>
<% controller.handleLogout(request, response); %>

<c:set var="isLoggedIn" value="<%=controller.isLoggedIn(request)%>"/>
<c:if test="${isLoggedIn}">
    <div id="authenticated" style="display: block" class="menu">
        <button name="logoutBtn" onclick="location.href = '<%= request.getContextPath() %>/index.jsp?action=logout'">Logout</button>
    </div>
</c:if>