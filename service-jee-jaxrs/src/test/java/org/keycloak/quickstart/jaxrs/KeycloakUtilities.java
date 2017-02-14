/*
 * JBoss, Home of Professional Open Source
 * Copyright 2017, Red Hat, Inc. and/or its affiliates, and individual
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

package org.keycloak.quickstart.jaxrs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.client.registration.Auth;
import org.keycloak.client.registration.ClientRegistration;
import org.keycloak.client.registration.ClientRegistrationException;
import org.keycloak.representations.idm.ClientRepresentation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;



public abstract class KeycloakUtilities {

    static class TypedList extends ArrayList<String> {
    };

    protected static String baseUrl;

    protected static String appName;

    protected static String clientConfiguration;

    protected static String registrationAccessCode;

    public static String createClient(ClientRepresentation clientRepresentation) {
        ClientRegistration reg = ClientRegistration.create()
                .url("http://localhost:8180/auth", "test-realm")
                .build();

        //for now we asssume the realm we are testing against as a white list policy for client registration
        //reg.auth(null);

        try {
            clientRepresentation = reg.create(clientRepresentation);
            registrationAccessCode = clientRepresentation.getRegistrationAccessToken();
            ObjectMapper mapper = new ObjectMapper();
            reg.auth(Auth.token(registrationAccessCode));
            clientConfiguration = mapper.writeValueAsString(reg.getAdapterConfig(clientRepresentation.getClientId()));
        } catch (ClientRegistrationException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return clientConfiguration;
    }

    public static void deleteClient(String clientId) {
        ClientRegistration reg = ClientRegistration.create()
                .url("http://localhost:8180/auth", "test-realm")
                .build();
        try {
            reg.auth(Auth.token(registrationAccessCode));
            reg.delete(clientId);
        } catch (ClientRegistrationException e) {
            e.printStackTrace();
        }
    }


    public boolean testGetWithAuth(String endpoint, String token) throws IOException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        ;
        try {
            HttpGet get = new HttpGet(baseUrl + endpoint);
            get.addHeader("Authorization", "Bearer " + token);

            HttpResponse response = client.execute(get);
            if (response.getStatusLine().getStatusCode() != 200) {
              return false;
            }
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            try {
               return true;
            } finally {
                is.close();
            }

        } finally {
            client.close();
        }
    }

    public boolean returnsForbidden(String endpoint) throws IOException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        try {
            HttpGet get = new HttpGet(baseUrl + endpoint);
            HttpResponse response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == 403 || response.getStatusLine().getStatusCode() == 401) {
                return true;
            } else {
                return false;
            }

        } finally {
            client.close();
        }
    }

    public String getToken(String username, String password, String realm, String clientID) {
        Keycloak keycloak = Keycloak.getInstance(
                "http://localhost:8180/auth",
                realm,
                username,
                password,
                clientID);
        return keycloak.tokenManager().getAccessTokenString();

    }

}





