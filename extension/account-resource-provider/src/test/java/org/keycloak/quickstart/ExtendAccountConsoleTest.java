/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.keycloak.quickstart;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.test.FluentTestsHelper;
import org.keycloak.test.page.LoginPage;
import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:aboullos@redhat.com">Alfredo Moises Boullosa</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ExtendAccountConsoleTest {

    public static final String KEYCLOAK_URL = "http://localhost:8180";

    @Page
    private LoginPage loginPage;

    @Page
    private ExtendedAccountPage accountPage;

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
    public static void afterClass() {
        if (testsHelper != null) {
            testsHelper.close();
        }
    }

    @AfterClass
    public static void cleanUp() {
        if (testsHelper != null) {
            testsHelper.deleteTestRealm();
        }
    }

    @Before
    public void setup() throws Exception {
        testsHelper.importTestRealm("/quickstart-realm.json");
        webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Test
    public void keycloakManThemeTest() {
        accountPage.navigateTo();
        Assert.assertTrue(accountPage.isLogoPresent());

        accountPage.clickOverviewHome();

        loginPage.login("test-admin", "password");
        Assert.assertTrue(accountPage.isOverviewPage());
        accountPage.clickKeycloakManApp();
        Assert.assertTrue(accountPage.isKeycloakManPage());
    }
}
