/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.quickstart.profilejee;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.keycloak.adapters.saml.SamlDeploymentContext;
import org.keycloak.adapters.saml.SamlPrincipal;
import org.keycloak.adapters.saml.SamlSession;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.constants.ServiceUrlConstants;

/**
 * Controller simplifies access to the server environment from the JSP.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2015 Red Hat Inc.
 */
public class Controller {

    public String getFirstName(HttpServletRequest req) {
        return getFriendlyAttrib(req, "givenName");
    }

    public String getLastName(HttpServletRequest req) {
        return getFriendlyAttrib(req, "surname");
    }

    public String getEmail(HttpServletRequest req) {
        return getFriendlyAttrib(req, "email");
    }

    public String getUsername(HttpServletRequest req) {
        return getAccount(req).getName();
    }

    private String getFriendlyAttrib(HttpServletRequest req, String attribName) {
        SamlPrincipal principal = getAccount(req);
        return principal.getFriendlyAttribute(attribName);
    }

    private SamlPrincipal getAccount(HttpServletRequest req) {
        SamlPrincipal principal = (SamlPrincipal)req.getUserPrincipal();
        return principal;
    }

    public boolean isLoggedIn(HttpServletRequest req) {
        return getAccount(req) != null;
    }

    public String getAccountUri(HttpServletRequest req) {
        String serverPath = findKeycloakServerPath(req);
        String realm = findRealmName(req);
        return KeycloakUriBuilder.fromUri(serverPath).path(ServiceUrlConstants.ACCOUNT_SERVICE_PATH)
                .queryParam("referrer", "app-profile-saml").build(realm).toString();
    }

    // HACK: This is a really bad way to find the realm name, but I can't
    //       figure out a better way to do it with the SAML adapter.  It parses
    //       the URL specified in keycloak-saml.xml
    private String findRealmName(HttpServletRequest req) {
        String bindingUrl = getBindingUrl(req);
        // bindingUrl looks like http://localhost:8080/auth/realms/master/protocol/saml
        int beginIndex = bindingUrl.indexOf("/realms/") + "/realms/".length();
        return bindingUrl.substring(beginIndex, bindingUrl.indexOf('/', beginIndex));
    }

    private String findKeycloakServerPath(HttpServletRequest req) {
        String bindingUrl = getBindingUrl(req);
        // bindingUrl looks like http://localhost:8080/auth/realms/master/protocol/saml
        return bindingUrl.substring(0, bindingUrl.indexOf("/auth")) + "/auth";
    }

    private String getBindingUrl(HttpServletRequest req) {
        SamlDeploymentContext ctx = (SamlDeploymentContext)req.getServletContext().getAttribute(SamlDeploymentContext.class.getName());
        return ctx.resolveDeployment(null).getIDP().getSingleSignOnService().getRequestBindingUrl();
    }

}
