package org.keycloak.quickstart.fuse.appjee;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.common.util.UriUtils;
import org.keycloak.constants.ServiceUrlConstants;

/**
 * Controller simplifies access to the server environment from the JSP.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2015 Red Hat Inc.
 */
public class Controller {

    private static final Logger log = Logger.getLogger(Controller.class.getName());

    public boolean isLoggedIn(HttpServletRequest req) {
        return getSession(req) != null;
    }

    public void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (isLogoutAction(req)) {
            req.logout();
            String unprotectedURL = getAppRootURL(req) + "/index.jsp";
            resp.sendRedirect(unprotectedURL);
        }
    }

    public boolean isLogoutAction(HttpServletRequest req) {
        return getAction(req).equals("logout");
    }

    public String getAccountUri(HttpServletRequest req) {
        KeycloakSecurityContext session = getSession(req);
        String baseUrl = getAuthServerBaseUrl(req);
        String realm = session.getRealm();
        return KeycloakUriBuilder.fromUri(baseUrl).path(ServiceUrlConstants.ACCOUNT_SERVICE_PATH)
                .queryParam("referrer", "fuse-app-jsp")
                .queryParam("referrer_uri", getReferrerUri(req)).build(realm).toString();
    }

    public String getServletPath(HttpServletRequest req) {
        if (isLoggedIn(req)) {
            return "protected";
        } else {
            return "index.jsp";
        }
    }

    private String getReferrerUri(HttpServletRequest req) {
        StringBuilder uri = new StringBuilder(getAppRootURL(req))
                .append("/").append(getServletPath(req));

        String q = req.getQueryString();
        if (q != null) {
            uri.append("?").append(q);
        }
        return uri.toString();
    }

    private String getAppRootURL(HttpServletRequest req) {
       return new StringBuilder(UriUtils.getOrigin(req.getRequestURL().toString()))
                .append(req.getContextPath())
                .toString();
    }

    private String getAuthServerBaseUrl(HttpServletRequest req) {
        AdapterDeploymentContext deploymentContext = (AdapterDeploymentContext) req.getServletContext().getAttribute(AdapterDeploymentContext.class.getName());
        KeycloakDeployment deployment = deploymentContext.resolveDeployment(null);
        return deployment.getAuthServerBaseUrl();
    }

    public String getMessage(HttpServletRequest req) {
        String action = getAction(req);
        if (action.equals("")) return "";
        if (isLogoutAction(req)) return "";

        try {
            return "Message: " + ServiceClient.callService(req, getSession(req), action);
        } catch (ServiceClient.Failure f) {
            log.warning("Failed to invoke serviceUrl: " + f.getRequestUri() + ". Error: " + f.getStatus() + " " + f.getReason());
            return "<span class='error'>" + f.getStatus() + " " + f.getReason() + "</span>";
        }
    }

    private KeycloakSecurityContext getSession(HttpServletRequest req) {
        return (KeycloakSecurityContext) req.getAttribute(KeycloakSecurityContext.class.getName());
    }

    private String getAction(HttpServletRequest req) {
        if (req.getParameter("action") == null) return "";
        return req.getParameter("action");
    }
}
