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
import org.keycloak.test.TestsHelper;
import org.keycloak.test.builders.ClientBuilder;

import java.io.File;
import java.io.IOException;

import static org.keycloak.test.TestsHelper.createClient;
import static org.keycloak.test.TestsHelper.createDirectGrantClient;
import static org.keycloak.test.TestsHelper.importTestRealm;
import static org.keycloak.test.builders.ClientBuilder.AccessType.BEARER_ONLY;


@RunWith(Arquillian.class)
public class ArquillianServiceJeeJaxrsTest {

    static {
        try {
            TestsHelper.appName = "test-demo";
            TestsHelper.baseUrl = "http://localhost:8080/test-demo";
            //TestsHelper.keycloakBaseUrl  = "set keycloak server docker IP"
            importTestRealm("admin", "admin", "/quickstart-realm.json");
            createDirectGrantClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Deployment(testable = false)
    public static Archive<?> createTestArchive() throws IOException {
        return ShrinkWrap.create(WebArchive.class, "test-demo.war")
                .addPackages(true, Filters.exclude(".*Test.*"), Application.class.getPackage())
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new StringAsset(createClient(ClientBuilder.create("test-demo")
                                .baseUrl(TestsHelper.baseUrl).accessType(BEARER_ONLY))), "keycloak.json")
                .setWebXML(new File("src/main/webapp", "WEB-INF/web.xml"));

    }

    @AfterClass
    public static void cleanUp() throws IOException {
        TestsHelper.deleteRealm("admin", "admin", TestsHelper.testRealm);
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
            Assert.assertTrue(TestsHelper.testGetWithAuth("/secured", TestsHelper.getToken("alice", "password", TestsHelper.testRealm)));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testAdminEndpointWithAuthButNoRole() {
        try {
            Assert.assertFalse(TestsHelper.testGetWithAuth("/admin", TestsHelper.getToken("alice", "password", TestsHelper.testRealm)));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testAdminEndpointWithAuthAndRole() {
        try {
            Assert.assertTrue(TestsHelper.testGetWithAuth("/admin", TestsHelper.getToken("test-admin", "password", TestsHelper.testRealm)));
        } catch (IOException e) {
            Assert.fail();
        }
    }
}
