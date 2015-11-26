/*
 * Copyright 2015 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.keycloak.quickstart.appjee;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.constants.ServiceUrlConstants;

/**
 * Controller simplifies access to the server environment from the JSP.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2015 Red Hat Inc.
 */
public class Controller {

    public boolean isLoggedIn(HttpServletRequest req) {
        return getSession(req) != null;
    }

    public void handleLogout(HttpServletRequest req) throws ServletException {
        if (isLogoutAction(req)) {
            req.logout();
        }
    }

    public boolean isLogoutAction(HttpServletRequest req) {
        return getAction(req).equals("logout");
    }

    public String getAccountUri(HttpServletRequest req) {
        KeycloakSecurityContext session = getSession(req);
        String realm = session.getRealm();
        return KeycloakUriBuilder.fromUri("/auth").path(ServiceUrlConstants.ACCOUNT_SERVICE_PATH)
                .queryParam("referrer", "app-jee").build(realm).toString();

    }

    public String getMessage(HttpServletRequest req) {
        String action = getAction(req);
        if (action.equals("")) return "";
        if (isLogoutAction(req)) return "";

        try {
            String message = ServiceClient.callService(req, getSession(req), action);
            return "MESSAGE: " + message.toUpperCase();
        } catch (ServiceClient.Failure f) {
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
