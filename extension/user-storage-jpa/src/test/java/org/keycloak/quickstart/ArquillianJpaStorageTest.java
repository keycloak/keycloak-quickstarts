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
import org.keycloak.representations.idm.ComponentRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.quickstart.test.FluentTestsHelper;
import org.keycloak.quickstart.test.page.LoginPage;
import org.keycloak.quickstart.storage.user.MyExampleUserStorageProviderFactory;
import org.keycloak.representations.idm.RequiredActionProviderRepresentation;
import java.time.Duration;
import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.FluentWait;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.openqa.selenium.support.ui.ExpectedConditions.not;
import static org.openqa.selenium.support.ui.ExpectedConditions.urlToBe;

@RunWith(Arquillian.class)
public class ArquillianJpaStorageTest {

    public static final String KEYCLOAK_URL = "http://localhost:8180";
    public static final String PROVIDER_TYPE = "org.keycloak.storage.UserStorageProvider";

    @Page
    private LoginPage loginPage;

    @Drone
    private WebDriver webDriver;

    private static FluentTestsHelper testsHelper;

    @BeforeClass
    public static void beforeClass() {
        testsHelper = new FluentTestsHelper(KEYCLOAK_URL,
                "admin", "admin",
                FluentTestsHelper.DEFAULT_ADMIN_REALM,
                FluentTestsHelper.DEFAULT_ADMIN_CLIENT,
                FluentTestsHelper.DEFAULT_TEST_REALM)
                .init();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        if (testsHelper != null) {
            testsHelper.close();
        }
    }

    @Before
    public void beforeTest() throws Exception {
        FluentTestsHelper r = testsHelper.importTestRealm("/quickstart-realm.json");
        RequiredActionProviderRepresentation ra = r.getTestRealmResource().flows().getRequiredAction("VERIFY_PROFILE");
        ra.setEnabled(false);
        r.getTestRealmResource().flows().updateRequiredAction("VERIFY_PROFILE", ra);
        webDriver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @After
    public void afterTest() {
        testsHelper.deleteTestRealm();
    }

    private void navigateTo(String path) {
        webDriver.navigate().to(testsHelper.getKeycloakBaseUrl() + path);
    }

    @Test
    public void testCreateUserInStorage() {
        final String providerId = addProvider();
        final String username = "joneill";
        final String password = "sgc-passwd";

        testsHelper.createTestUser(username, password);
        // The MyUserStorageProvider doesn't implement all methods, e.g. searchForUserStream using attributes is missing, therefore we
        // need to use a different interface, in this case using "search" attribute and pagination
        UserRepresentation fetchedUser = testsHelper.getTestRealmResource().users().search(username, 0, 1).get(0);

        // check if the user is created using the storage provider
        assertEquals(providerId, fetchedUser.getOrigin());

        // test if login works
        navigateToAccount(username, password);
    }

    private String addProvider() {
        ComponentRepresentation provider = new ComponentRepresentation();
        provider.setProviderId(MyExampleUserStorageProviderFactory.PROVIDER_ID);
        provider.setProviderType(PROVIDER_TYPE);
        provider.setName(MyExampleUserStorageProviderFactory.PROVIDER_ID);

        Response response = testsHelper.getTestRealmResource().components().add(provider);
        assertEquals(201, response.getStatus());

        return testsHelper.getCreatedId(response);
    }

    private void navigateToAccount(String user, String password) {
        navigateTo(format("/realms/%s/account/#/", testsHelper.getTestRealmName()));
        waitForPageToLoad();
        loginPage.login(user, password);
    }

    public void waitForPageToLoad() {
        // Taken from org.keycloak.testsuite.util.WaitUtils

        String currentUrl = null;

        // Ensure the URL is "stable", i.e. is not changing anymore; if it'd changing, some redirects are probably still in progress
        for (int maxRedirects = 4; maxRedirects > 0; maxRedirects--) {
            currentUrl = webDriver.getCurrentUrl();
            FluentWait<WebDriver> wait = new FluentWait<>(webDriver).withTimeout(Duration.ofMillis(250));
            try {
                wait.until(not(urlToBe(currentUrl)));
            }
            catch (TimeoutException e) {
                break; // URL has not changed recently - ok, the URL is stable and page is current
            }
        }
    }
}
