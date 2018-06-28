/*
 * JBoss, Home of Professional Open Source
 *
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.keycloak.quickstart.springboot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.authorization.client.AuthorizationDeniedException;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.keycloak.test.TestsHelper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {MyApplication.class})
public class MyAppTest {

    @BeforeClass
    public static void setup() throws IOException {
        TestsHelper.baseUrl = "http://localhost:8080";
        TestsHelper.testRealm="spring-boot-quickstart";
        TestsHelper.importTestRealm("admin","admin","/quickstart-realm.json");
        TestsHelper.createDirectGrantClient();
    }

    @AfterClass
    public static void cleanUp() throws IOException{
        TestsHelper.deleteRealm("admin","admin", "spring-boot-quickstart");
    }

    @Test
    public void testAliceAccess() throws IOException {
        // alice can access her information
        HttpResponse response = makeRequest("http://localhost:8080/api/alice", "alice", "alice");
        assertAccessGranted(response, "alice");

        // alice can not access information about jdoe
        response = makeRequest("http://localhost:8080/api/jdoe", "alice", "alice");
        assertEquals(403, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testJdoeAccess() throws IOException {
        // alice can access her information
        HttpResponse response = makeRequest("http://localhost:8080/api/jdoe", "jdoe", "jdoe");
        assertAccessGranted(response, "jdoe");

        // jdoe can access information about alice because he is granted with "people-manager" role
        response = makeRequest("http://localhost:8080/api/alice", "jdoe", "jdoe");
        assertAccessGranted(response, "alice");
    }

    private HttpResponse makeRequest(String uri, String userName, String password) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(uri);
        String accessToken = TestsHelper.getToken(userName, password, TestsHelper.testRealm);

        request.addHeader("Authorization", "Bearer " + accessToken);

        return client.execute(request);
    }

    private void assertAccessGranted(HttpResponse response, String username) throws IOException {
        assertEquals(200, response.getStatusLine().getStatusCode());
        assertTrue(EntityUtils.toString(response.getEntity()).contains("\"name\":\"" + username + "\""));
    }
}
