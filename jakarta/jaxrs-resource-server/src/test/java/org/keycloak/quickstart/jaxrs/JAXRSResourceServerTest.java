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
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.test.TestsHelper;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.keycloak.test.TestsHelper.deleteRealm;
import static org.keycloak.test.TestsHelper.importTestRealm;


@RunWith(Arquillian.class)
public class JAXRSResourceServerTest {

    @ArquillianResource
    private URL contextRoot;

    @Deployment(testable = false)
    public static Archive<?> createTestArchive() throws IOException {
        return ShrinkWrap.create(ZipImporter.class, "jakarta-jaxrs-resource-server.war").importFrom(
                        new File("target/jakarta-jaxrs-resource-server.war")).as(WebArchive.class)
                .addAsWebInfResource("oidc.json");

    }

    @AfterClass
    public static void cleanUp() throws Exception {
        deleteRealm("admin", "admin", "quickstart");
    }

    @BeforeClass
    public static void onBeforeClass() {
        try {
            importTestRealm("admin", "admin", "/quickstart-realm.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Before
    public void onBefore() {
        TestsHelper.baseUrl = contextRoot.toString();
        TestsHelper.testRealm = "quickstart";
    }

    @Test
    public void testSecuredEndpoint() {
        try {
            Assert.assertTrue(TestsHelper.returnsForbidden("/secured"));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testAdminEndpoint() {
        try {
            Assert.assertTrue(TestsHelper.returnsForbidden("/admin"));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testPublicEndpoint() {
        try {
            Assert.assertFalse(TestsHelper.returnsForbidden("/public"));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testSecuredEndpointWithAuth() {
        try {
            Assert.assertTrue(TestsHelper.testGetWithAuth("/secured", getToken("alice", "alice")));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testAdminEndpointWithAuthButNoRole() {
        try {
            Assert.assertFalse(TestsHelper.testGetWithAuth("/admin", getToken("alice", "alice")));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testAdminEndpointWithAuthAndRole() {
        try {
            Assert.assertTrue(TestsHelper.testGetWithAuth("/admin", getToken("admin", "admin")));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    public String getToken(String username, String password) {
        Keycloak keycloak = Keycloak.getInstance(
                TestsHelper.keycloakBaseUrl,
                TestsHelper.testRealm,
                username,
                password,
                "jakarta-jaxrs-resource-server",
                "secret");
        return keycloak.tokenManager().getAccessTokenString();

    }
}
