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
package org.keycloak.quickstart.fuse.camel;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.AdapterUtils;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.util.JsonSerialization;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.Response.Status;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CamelHelloProcessor implements Processor {

    public static Map<String, String> ID_TO_ROLE = new HashMap<>();

    static {
        ID_TO_ROLE.put("secured", "user");
        ID_TO_ROLE.put("admin", "admin");
    }

    private boolean checkAccess(KeycloakPrincipal keycloakPrincipal, String id) {
        // TODO: update after fixing https://issues.jboss.org/browse/KEYCLOAK-8045

        String expectedRole = ID_TO_ROLE.get(id);
        if (expectedRole == null) {
            return true;
        }

        KeycloakSecurityContext ksc = keycloakPrincipal.getKeycloakSecurityContext();
        if (ksc instanceof RefreshableKeycloakSecurityContext) {
            Set<String> roles = AdapterUtils.getRolesFromSecurityContext((RefreshableKeycloakSecurityContext) ksc);
            return roles.contains(expectedRole);
        }

        return false;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        // KeycloakSecurityContext encapsulates informations like token etc
        KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) exchange.getProperty(KeycloakPrincipal.class.getName(), KeycloakPrincipal.class);

        String suffix = (String) exchange.getIn().getHeader("id");

        if (! checkAccess(keycloakPrincipal, suffix)) {
            exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Status.FORBIDDEN.getStatusCode());
            return;
        }

        exchange.getOut().setHeader(Exchange.CONTENT_TYPE, "application/json");
        Message message = new Message("camel - " + suffix);
        String jsonResponse = JsonSerialization.writeValueAsString(message);
        exchange.getOut().setBody(jsonResponse);
    }
}
