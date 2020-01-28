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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.test.builders.ClientBuilder;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.File;

import static org.keycloak.test.builders.ClientBuilder.AccessType.BEARER_ONLY;
import static org.keycloak.test.builders.ClientBuilder.AccessType.PUBLIC;


@RunWith(Arquillian.class)
public class ArquillianServiceJeeJaxrsTest {

    public static final String APP_SERVER_URL = "http://localhost:8080";

    public static final String TEST_APP_NAME = "test-demo";
    public static final String TEST_DGA = "test-dga";
    public static final String TEST_REALM = "quickstart";

    public static final String APP_URL = APP_SERVER_URL + "/" + TEST_APP_NAME;

    public static final JaxrsTestHelper testHelper = new JaxrsTestHelper();

    static {
        try {
            testHelper.init();
            testHelper.importTestRealm("/quickstart-realm.json")
                    .createClient(ClientBuilder.create(TEST_DGA).accessType(PUBLIC))
                    .createClient(ClientBuilder.create(TEST_APP_NAME).baseUrl(APP_URL).accessType(BEARER_ONLY));
        } catch (Exception e) {
            throw new IllegalStateException("Could not initialize Keycloak", e);
        }
    }

    @Deployment(testable = false)
    public static Archive<?> createTestArchive() throws Exception {
        return ShrinkWrap.create(WebArchive.class, "test-demo.war")
                .addPackages(true, Filters.exclude(".*Test.*"), Application.class.getPackage())
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new StringAsset(testHelper.getAdapterConfiguration(TEST_APP_NAME)), "keycloak.json")
                .setWebXML(new File("src/main/webapp", "WEB-INF/web.xml"));

    }

    @AfterClass
    public static void cleanUp() {
        testHelper.deleteRealm(TEST_REALM);
    }

    @Test
    public void testSecuredEndpoint() {
        Assert.assertEquals(401, get(APP_URL + "/secured").getStatus());
    }

    @Test
    public void testAdminEndpoint() {
        Assert.assertEquals(401, get(APP_URL + "/admin").getStatus());
    }

    @Test
    public void testPublicEndpoint() {
        Assert.assertEquals(200, get(APP_URL + "/public").getStatus());
    }

    @Test
    public void testSecuredEndpointWithAuth() {
        String tokenForAlice = new JaxrsTestHelper("alice", "password", TEST_REALM, TEST_DGA).initWithoutInitialToken().getToken();
        Assert.assertEquals(200, get(APP_URL + "/secured", tokenForAlice).getStatus());
    }

    @Test
    public void testAdminEndpointWithAuthButNoRole() {
        String tokenForAlice = new JaxrsTestHelper("alice", "password", TEST_REALM, TEST_DGA).initWithoutInitialToken().getToken();
        Assert.assertEquals(403, get(APP_URL + "/admin", tokenForAlice).getStatus());
    }

    @Test
    public void testAdminEndpointWithAuthAndRole() {
        String tokenForTestAdmin = new JaxrsTestHelper("test-admin", "password", TEST_REALM, TEST_DGA).initWithoutInitialToken().getToken();
        Assert.assertEquals(200, get(APP_URL + "/admin", tokenForTestAdmin).getStatus());
    }

    public Response get(String uri) {
        return get(uri, null);
    }

    public Response get(String uri, String token) {
        Client client = javax.ws.rs.client.ClientBuilder.newClient();
        Response response = null;
        try {
            WebTarget target = client.target(uri);
            Invocation.Builder request = target.request();
            if (token != null)
                request.header("Authorization", "Bearer " + token);
            response = request.get();
            response.close();
        } finally {
            client.close();
        }
        return response;
    }

}
