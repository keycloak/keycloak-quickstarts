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
package org.keycloak.quickstart.fuse.cxf.rs;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.AdapterUtils;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;


@Path("/")
public class Resource {

    // This is necessary for standalone Undertow engine managed by CXF.
    // For CXF endpoints managed by Pax Web, use Dynamic context manipulation
    // (see https://ops4j1.jira.com/browse/PAXWEB-1167)
    private void checkAccess(KeycloakPrincipal keycloakPrincipal, String expectedRole) {
        KeycloakSecurityContext ksc = keycloakPrincipal.getKeycloakSecurityContext();
        if (ksc instanceof RefreshableKeycloakSecurityContext) {
            Set<String> roles = AdapterUtils.getRolesFromSecurityContext((RefreshableKeycloakSecurityContext) ksc);
            if (! roles.contains(expectedRole)) {
                throw new javax.ws.rs.ForbiddenException();
            }
        } else {
            throw new javax.ws.rs.ForbiddenException();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("public")
    public Message getPublic(@Context HttpHeaders header, @Context HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        return new Message("cxf - public");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("secured")
    public Message getSecured(@Context HttpServletRequest request) throws Exception {
        checkAccess((KeycloakPrincipal) request.getUserPrincipal(), "user");
        return new Message("cxf - secured");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("admin")
    public Message getAdmin(@Context HttpServletRequest request) {
        checkAccess((KeycloakPrincipal) request.getUserPrincipal(), "admin");
        return new Message("cxf - admin");
    }

}
