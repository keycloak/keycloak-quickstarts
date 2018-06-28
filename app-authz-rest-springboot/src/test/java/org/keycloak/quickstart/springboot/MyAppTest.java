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
    public void testAccessToPathsMappedWithDefaultResource() throws IOException {
        // accessing resource using a regular access token
        HttpResponse response = makeRequest("http://localhost:8080/api/resourcea", "alice", "alice", false, "Default Resource");
        assertAccessGranted(response);
        response = makeRequest("http://localhost:8080/api/resourceb", "alice", "alice", false, "Default Resource");
        assertAccessGranted(response);


        // accessing resource using an RPT
        response = makeRequest("http://localhost:8080/api/resourcea", "alice", "alice", true, "Default Resource");
        assertAccessGranted(response);
        response = makeRequest("http://localhost:8080/api/resourceb", "alice", "alice", true, "Default Resource");
        assertAccessGranted(response);

        // jdoe should access /api/resourcea and /api/resourceb
        response = makeRequest("http://localhost:8080/api/resourcea", "jdoe", "jdoe", false, "Default Resource");
        assertAccessGranted(response);
        response = makeRequest("http://localhost:8080/api/resourceb", "jdoe", "jdoe", false, "Default Resource");
        assertAccessGranted(response);
    }

    @Test
    public void testAccessToPathsMappedWithPremiumResource() throws IOException {
        // accessing resource with regular access token
        HttpResponse response = makeRequest("http://localhost:8080/api/premium", "jdoe", "jdoe", false, "Premium Resource");
        assertAccessGranted(response);

        // accessing resource with an RPT
        response = makeRequest("http://localhost:8080/api/premium", "jdoe", "jdoe", true, "Premium Resource");
        assertAccessGranted(response);

        try {
            // alice can't access the requested resource because only "premium" users are allowed to do so
            response = makeRequest("http://localhost:8080/api/premium", "alice", "alice", true, "Premium Resource");
            fail("Should fail, user alice is not supposed to have access to premium resources (missing user-premium role)");
        } catch (AuthorizationDeniedException ignore) {
        }
    }

    @Test
    public void testAccessToUnknownPathsShouldBeDenied() throws IOException {
        HttpResponse response = makeRequest("http://localhost:8080/unknownResource", "alice", "alice", true, "Default Resource");
        assertEquals(403, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testAccessToPathsMappedWithAdminResource() throws IOException {
        // accessing resource with regular access token
        HttpResponse response = makeRequest("http://localhost:8080/api/admin?parameter-a=claim-value", "alice", "alice", false, "Admin Resource");
        assertAccessGranted(response);

        try {
            // alice can't access the requested resource because request parameter 'parameter-a' is missing
            response = makeRequest("http://localhost:8080/api/admin", "alice", "alice", true, "Admin Resource");
            fail("Should fail, user alice is not supposed to have access to premium resources (missing user-premium role)");
        } catch (AuthorizationDeniedException ignore) {
        }

        try {
            // alice can't access the requested resource because request parameter 'parameter-a' has an unexpected value
            response = makeRequest("http://localhost:8080/api/admin?parameter-a=unexpected", "alice", "alice", true, "Admin Resource");
            fail("Should fail, user alice is not supposed to have access to premium resources (missing user-premium role)");
        } catch (AuthorizationDeniedException ignore) {
        }
    }

    @Test
    public void testAccessWithoutBearerShouldBeDenied() throws IOException {
        HttpResponse httpResponse = makeRequestWithoutBearer("http://localhost:8080/api/admin");
        assertEquals(403, httpResponse.getStatusLine().getStatusCode());
    }

    private HttpResponse makeRequest(String uri, String userName, String password, boolean sendRpt, String resourceId) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(uri);
        String accessToken = TestsHelper.getToken(userName, password, TestsHelper.testRealm);
        String rpt;

        if (sendRpt) {
            rpt = obtainRequestingPartyToken(resourceId, accessToken);
        } else {
            rpt = accessToken;
        }

        request.addHeader("Authorization", "Bearer " + rpt);

        return client.execute(request);
    }

    private HttpResponse makeRequestWithoutBearer(String uri) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(uri);

        return client.execute(request);
    }

    /**
     * Obtain a RPT from a Keycloak server. The RPT is a result of the evaluation of all policies associated with the resources being
     * requested as defined by <code>permissionRequest</code>.
     *
     * @param permissionRequest the permission request
     * @param accessToken an OAuth2 access token previously issued by Keycloak
     * @return
     */
    private String obtainRequestingPartyToken(String resourceId, String accessToken) {
        Configuration configuration = new Configuration();

        configuration.setResource("app-authz-rest-springboot");
        configuration.setAuthServerUrl(TestsHelper.keycloakBaseUrl);
        configuration.setRealm(TestsHelper.testRealm);

        AuthzClient authzClient = AuthzClient.create(configuration);

        AuthorizationRequest request = new AuthorizationRequest();

        request.addPermission(resourceId);

        return authzClient.authorization(accessToken).authorize(request).getToken();
    }

    private void assertAccessGranted(HttpResponse response) throws IOException {
        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals("Access Granted", EntityUtils.toString(response.getEntity()));
    }
}
