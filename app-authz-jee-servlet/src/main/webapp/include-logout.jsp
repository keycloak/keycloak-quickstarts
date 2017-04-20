<%@ page import="org.keycloak.KeycloakSecurityContext" %>
<%@ page import="org.keycloak.common.util.KeycloakUriBuilder" %>
<%@ page import="org.keycloak.constants.ServiceUrlConstants" %>
<%
    KeycloakSecurityContext kcContext = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
%>
<h2><a href="<%= KeycloakUriBuilder.fromUri(kcContext.getToken().getIssuer().substring(0, kcContext.getToken().getIssuer().indexOf("/realms"))).path(ServiceUrlConstants.TOKEN_SERVICE_LOGOUT_PATH)
            .queryParam("redirect_uri", request.getScheme() + "://" + ("127.0.0.1".equals(request.getLocalName()) ? "localhost" : request.getLocalName()) + ":" + request.getLocalPort() + request.getContextPath()).build(kcContext.getToken().getIssuer().substring(kcContext.getToken().getIssuer().lastIndexOf('/') + 1)).toString()%>">Logout</a></h2>