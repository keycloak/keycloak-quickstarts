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
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.representations.idm.ClientRepresentation;

import java.io.File;
import java.io.IOException;


@RunWith(Arquillian.class)
public class ArquillianTest extends KeycloakUtilities {


    @Deployment(testable = false)
    public static Archive<?> createTestArchive() {
         return ShrinkWrap.create(WebArchive.class,  "test-demo.war")
                .addPackages(true, Filters.exclude(".*Test.*"),Application.class.getPackage())
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new StringAsset(createClient(generateClientRepresentation())), "keycloak.json")
                .setWebXML(new File("src/main/webapp", "WEB-INF/web.xml"));

    }

    @BeforeClass
    public static void setup() {
        appName = "test-demo";
        baseUrl = "http://localhost:8080/test-demo";
    }

    @Test()
    public void testSecuredEndpoint()  {
        try {
            Assert.assertTrue(returnsForbidden("/secured"));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test()
    public void testAdminEndpoint()  {
        try {
            Assert.assertTrue(returnsForbidden("/admin"));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test()
    public void testPublicEndpoint()  {
        try {
            Assert.assertFalse(returnsForbidden("/public"));
            System.out.println(getToken("testuser","password","test-realm", "test-dga"));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test()
    public void testSecuredEndpointWithAuth()  {
        try {
            Assert.assertTrue(testGetWithAuth("/secured",getToken("testuser","password","test-realm", "test-dga")));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test()
    public void testAdminEndpointWithAuthButNoRole()  {
        try {
            Assert.assertFalse(testGetWithAuth("/admin",getToken("testuser","password","test-realm", "test-dga")));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test()
    public void testAdminEndpointWithAuthAndRole()  {
        try {
            Assert.assertTrue(testGetWithAuth("/admin",getToken("admin","password","test-realm", "test-dga")));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    public static ClientRepresentation generateClientRepresentation() {
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setClientId("test-demo");
        //clientRepresentation.setBaseUrl(getBaseUrl());
        clientRepresentation.setBearerOnly(true);
        return clientRepresentation;
    }

    @AfterClass
    public static void cleanUp() {
        deleteClient(appName);
    }

}
