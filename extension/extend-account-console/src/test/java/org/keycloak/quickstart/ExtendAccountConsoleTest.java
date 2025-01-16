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

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.*;
import org.junit.runner.RunWith;
import org.keycloak.quickstart.test.FluentTestsHelper;
import org.keycloak.quickstart.test.page.LoginPage;
import java.time.Duration;
import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.FluentWait;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.not;
import static org.openqa.selenium.support.ui.ExpectedConditions.urlToBe;

/**
 * @author <a href="mailto:aboullos@redhat.com">Alfredo Moises Boullosa</a>
 */
@RunWith(Arquillian.class)
@Ignore
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
    public void keycloakManThemeTest() throws Exception {
        accountPage.navigateTo();
        waitForPageToLoad();
        assertThat(webDriver.getTitle(), containsString("Sign in to " + testsHelper.getTestRealmName()));

        loginPage.login("test-admin", "password");
        waitForPageToLoad();

        Assert.assertTrue(accountPage.isLogoPresent());
        accountPage.clickKeycloakManContainer();
        accountPage.clickOverviewHome();

        Assert.assertTrue(accountPage.isOverviewPage());
        accountPage.clickKeycloakManApp();

        Assert.assertTrue(accountPage.isKeycloakManPage());
        accountPage.clickKeycloakManLovesJsx();

        assertThat(webDriver.getPageSource(), containsString("Keycloak Man Loves JSX, React, and PatternFly"));
        Assert.assertTrue(accountPage.isKeycloakManPage());
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
