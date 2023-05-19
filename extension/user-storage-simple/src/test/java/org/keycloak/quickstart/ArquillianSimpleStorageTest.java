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

package org.keycloak.quickstart;

import jakarta.ws.rs.core.Response;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.quickstart.page.ConsolePage;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.test.FluentTestsHelper;
import org.keycloak.test.page.LoginPage;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.keycloak.quickstart.util.StorageManager.addUser;
import static org.keycloak.quickstart.util.StorageManager.createStorage;
import static org.keycloak.quickstart.util.StorageManager.deleteStorage;
import static org.keycloak.quickstart.util.StorageManager.getPropertyFile;


@RunWith(Arquillian.class)
public class ArquillianSimpleStorageTest {

    public static final String KEYCLOAK_URL = "http://localhost:8180";

    @Page
    private LoginPage loginPage;

    @Page
    private ConsolePage consolePage;

    @Drone
    private WebDriver webDriver;

    private static FluentTestsHelper testsHelper;

    @BeforeClass
    public static void beforeTestClass() throws IOException {
        testsHelper = new FluentTestsHelper(KEYCLOAK_URL,
                "admin", "admin",
                FluentTestsHelper.DEFAULT_ADMIN_REALM,
                FluentTestsHelper.DEFAULT_ADMIN_CLIENT,
                FluentTestsHelper.DEFAULT_TEST_REALM)
                .init();
    }

    @AfterClass
    public static void afterTestClass() {
        if (testsHelper != null) {
            testsHelper.close();
        }
    }

    @Before
    public void beforeTest() throws IOException {
        testsHelper.importTestRealm("/quickstart-realm.json");
        webDriver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @After
    public void afterTest() {
        testsHelper.deleteTestRealm();
        deleteStorage(); // the storage must not be deleted before realm
    }

    private void navigateTo(String path) {
        webDriver.navigate().to(KEYCLOAK_URL + path);
    }

    @Test
    public void testUserReadOnlyFederationStorage() {
        addProvider(org.keycloak.quickstart.readonly.PropertyFileUserStorageProviderFactory.PROVIDER_NAME);
        assertEquals("There should be no tbrady user", 0, testsHelper.getTestRealmResource().users().search("tbrady").size());

        navigateToAccount("tbrady", "superbowl");
        assertEquals("Should display the user from storage provider", "tbrady", consolePage.getUser());
        consolePage.logout();
    }

    @Test
    public void testUserWritableFederationStorage() {
        assertEquals("There should be two users", 2, (long) testsHelper.getTestRealmResource().users().count());
        assertEquals("There should be two users listed", 2, testsHelper.getTestRealmResource().users().list().size());
        assertEquals("There should be no malcom user", 0, testsHelper.getTestRealmResource().users().search("malcom").size());
        assertEquals("There should be no rob user", 0, testsHelper.getTestRealmResource().users().search("rob").size());

        createStorage();
        addUser("malcom", "butler");
        addProvider(org.keycloak.quickstart.writeable.PropertyFileUserStorageProviderFactory.PROVIDER_NAME);

        navigateToAccount("malcom", "butler");
        assertEquals("Should display the user from storage provider", "malcom", consolePage.getUser());
        consolePage.logout();

        addUser("rob", "gronkowski");
        navigateToAccount("rob", "gronkowski");
        assertEquals("Should display the user from storage provider", "rob", consolePage.getUser());
        consolePage.logout();

        assertEquals("There should be two users", 4, (long) testsHelper.getTestRealmResource().users().count());
        assertEquals("There should be two users listed", 4, testsHelper.getTestRealmResource().users().list().size());

        List<UserRepresentation> list = testsHelper.getTestRealmResource().users().list(2, 2);
        assertEquals("There should be two users listed", 2, list.size());
        assertEquals("First user should be malcom", "malcom", list.get(0).getUsername());
        assertEquals("Second user should be rob", "rob", list.get(1).getUsername());
    }

    private void addProvider(String providerId) {
        ComponentRepresentation provider = new ComponentRepresentation();
        provider.setProviderId(providerId);
        provider.setProviderType("org.keycloak.storage.UserStorageProvider");
        provider.setName(providerId);

        if (org.keycloak.quickstart.writeable.PropertyFileUserStorageProviderFactory.PROVIDER_NAME.equals(providerId)) {
            provider.setConfig(new MultivaluedHashMap<String, String>() {{
                putSingle("path", getPropertyFile());
            }});
        }

        Response response = testsHelper.getTestRealmResource().components().add(provider);
        assertEquals(201, response.getStatus());
    }

    private void navigateToAccount(String user, String password) {
        navigateTo(format("/realms/%s/account/#/personal-info", testsHelper.getTestRealmName()));
        loginPage.login(user, password);
    }
}