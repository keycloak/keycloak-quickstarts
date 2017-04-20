<%@ page import="org.keycloak.KeycloakSecurityContext" %>
<%@ page import="org.keycloak.common.util.KeycloakUriBuilder" %>
<h2><a href="<%= KeycloakUriBuilder.fromUri(keycloakSecurityContext.getToken().getIssuer().substring(0, keycloakSecurityContext.getToken().getIssuer().indexOf("/realms"))).path(ServiceUrlConstants.TOKEN_SERVICE_LOGOUT_PATH)
            .queryParam("redirect_uri", request.getScheme() + "://" + ("127.0.0.1".equals(request.getLocalName()) ? "localhost" : request.getLocalName()) + ":" + request.getLocalPort() + request.getContextPath()).build(keycloakSecurityContext.getToken().getIssuer().substring(keycloakSecurityContext.getToken().getIssuer().lastIndexOf('/') + 1)).toString()%>">Logout</a></h2>
