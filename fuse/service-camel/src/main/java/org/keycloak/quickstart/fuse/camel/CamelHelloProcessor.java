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

import javax.servlet.http.HttpServletRequest;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.util.JsonSerialization;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CamelHelloProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        HttpServletRequest req = exchange.getIn().getBody(HttpServletRequest.class);

        // KeycloakPrincipal encapsulates informations like token etc
        KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) req.getUserPrincipal();

        String suffix = (String) exchange.getIn().getHeader("id");

        exchange.getOut().setHeader(Exchange.CONTENT_TYPE, "application/json");
        Message message = new Message("camel - " + suffix);
        String jsonResponse = JsonSerialization.writeValueAsString(message);
        exchange.getOut().setBody(jsonResponse);
    }
}
