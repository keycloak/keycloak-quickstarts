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
package org.keycloak.quickstart.profilejee;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.keycloak.adapters.saml.SamlDeploymentContext;
import org.keycloak.adapters.saml.SamlSession;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.constants.ServiceUrlConstants;

/**
 * Controller simplifies access to the server environment from the JSP.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2015 Red Hat Inc.
 */
public class Controller {

    public void printAccount(HttpServletRequest req) {
        SamlSession session = getAccount(req);
        System.out.println("friendly names=" + session.getPrincipal().getFriendlyNames());
        System.out.println("attribute names=" + session.getPrincipal().getAttributeNames());
        System.out.println("-------- attribs --------------");
        for (String attr : session.getPrincipal().getAttributeNames()) {
            System.out.println(attr + "=" + session.getPrincipal().getAttribute(attr));
        }
        System.out.println("------- session map ------------");
        java.util.Enumeration<String> sessAttrs = req.getSession().getAttributeNames();
        while (sessAttrs.hasMoreElements()) {
            String attr = sessAttrs.nextElement();
            System.out.println(attr + "=" + req.getSession().getAttribute(attr));
        }

        System.out.println("------- app map ----------");
        java.util.Enumeration<String> appAttribs = req.getServletContext().getAttributeNames();
        while (appAttribs.hasMoreElements()) {
            String attr = appAttribs.nextElement();
            System.out.println(attr + "=" + req.getServletContext().getAttribute(attr));
        }
    }

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
        return req.getUserPrincipal().getName();
    }

    private String getFriendlyAttrib(HttpServletRequest req, String attribName) {
        SamlSession session = getAccount(req);
        return session.getPrincipal().getFriendlyAttribute(attribName);
    }

    private SamlSession getAccount(HttpServletRequest req) {
        HttpSession session = req.getSession();
        return (SamlSession)session.getAttribute(SamlSession.class.getName());
    }

    public boolean isLoggedIn(HttpServletRequest req) {
        return getAccount(req) != null;
    }

    public String getAccountUri(HttpServletRequest req) {
        String realm = findRealmName(req);
        System.out.println(">>>> reamName=" + realm);
        return KeycloakUriBuilder.fromUri("/auth").path(ServiceUrlConstants.ACCOUNT_SERVICE_PATH)
                .queryParam("referrer", "app-profile-jee-saml").build(realm).toString();
    }

    // HACK: This is a really bad way to find the realm name, but I can't
    //       figure out a better way to do it with the SAML adapter.  It parses
    //       the URL specified in keycloak-saml.xml
    private String findRealmName(HttpServletRequest req) {
        SamlDeploymentContext ctx = (SamlDeploymentContext)req.getServletContext().getAttribute(SamlDeploymentContext.class.getName());
        String bindingUrl = ctx.resolveDeployment(null).getIDP().getSingleSignOnService().getRequestBindingUrl();
        // bindingUrl looks like http://localhost:8080/auth/realms/master/protocol/saml
        int beginIndex = bindingUrl.indexOf("/realms/") + "/realms/".length();
        return bindingUrl.substring(beginIndex, bindingUrl.indexOf('/', beginIndex));
    }

}
