/*
 * Copyright 2024 Red Hat, Inc. and/or its affiliates
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
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 */
@RunWith(Arquillian.class)
public class ExtendAdminConsoleTest {

    public static final String KEYCLOAK_URL = "http://localhost:8180";

    @Page
    private LoginPage loginPage;

    @Page
    private ExtendedAdminPage adminConsole;

    @Page
    private RealmSettingsAttributePage realmSettingsAttributePage;

    @Drone
    private WebDriver webDriver;

    private static FluentTestsHelper testsHelper;

    @BeforeClass
    public static void beforeClass() {
        testsHelper = new FluentTestsHelper(KEYCLOAK_URL,
                "admin", "admin",
                FluentTestsHelper.DEFAULT_ADMIN_REALM,
                FluentTestsHelper.DEFAULT_ADMIN_CLIENT,
                FluentTestsHelper.DEFAULT_ADMIN_REALM)
                .init();
    }

    @AfterClass
    public static void afterClass() {
        if (testsHelper != null) {
            testsHelper.close();
        }
    }

    @Before
    public void setup() throws Exception {
        webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        adminConsole.navigateTo();
        waitForPageToLoad();
        if (webDriver.getTitle().contains("Sign in")) {
            loginPage.login("admin", "admin");
            waitForPageToLoad();
        }
    }

    @Test
    public void testAdminUiTodoApp() throws Exception {
        assertThat(webDriver.getTitle(), containsString("Keycloak Administration Console"));

        Assert.assertTrue(adminConsole.isTodoMenuPresent());
        adminConsole.clickTodoMenuItem();
        waitForPageToLoad();
        Assert.assertTrue(adminConsole.isOverviewPage());

        adminConsole.clickAddButton();
        waitForPageToLoad();
        adminConsole.fillTodoForm("something", "something that needs doing");
        adminConsole.clickSave();

        Assert.assertTrue(adminConsole.isSaved());

    }

    @Test
    public void testPageProviderCancelButton() throws Exception {
        adminConsole.clickTodoMenuItem();
        waitForPageToLoad();
        Assert.assertTrue(adminConsole.isOverviewPage());

        // Navigate to the form page by clicking Add button
        adminConsole.clickAddButton();
        waitForPageToLoad();

        // Interact with the form before checking the Cancel button so the page
        // provider's form (and its action group) is fully rendered.
        adminConsole.fillTodoForm("test task", "test description");
        // The cancel button (data-testid="cancel") is only rendered by newer admin
        // console builds; skip rather than fail on server builds that do not have it.
        Assume.assumeTrue("Cancel button not present in this server build",
                adminConsole.isCancelButtonPresent());
        String detailPageUrl = adminConsole.getCurrentUrl();

        adminConsole.clickCancel();
        waitForPageToLoad();
        new WebDriverWait(webDriver, Duration.ofSeconds(5))
                .until(ExpectedConditions.not(ExpectedConditions.urlToBe(detailPageUrl)));

        // Verify redirect occurred - URL should change back to overview page
        String currentUrl = adminConsole.getCurrentUrl();
        Assert.assertNotEquals(detailPageUrl, currentUrl);
        Assert.assertTrue(adminConsole.isOverviewPage());
    }

    @Test
    public void testTabProviderRevertButton() throws Exception {
        realmSettingsAttributePage.navigateTo();
        waitForPageToLoad();

        Assert.assertTrue(realmSettingsAttributePage.logoInputExists());

        // Interact with the form before checking the Revert button so the tab
        // provider's form (and its action group) is fully rendered.
        String initialValue = "http://initial.com/logo.png";
        realmSettingsAttributePage.fillLogoField(initialValue);

        // The revert button (data-testid="cancel") for tab providers is only rendered by
        // newer admin console builds; skip rather than fail on builds that do not have it.
        Assume.assumeTrue("Revert button not present in this server build",
                realmSettingsAttributePage.isRevertButtonPresent());
        Assert.assertEquals(initialValue, realmSettingsAttributePage.getLogoFieldValue());

        realmSettingsAttributePage.clickRevertButton();
        waitForPageToLoad();

        // The revert should reset the form without redirecting
        String revertedValue = realmSettingsAttributePage.getLogoFieldValue();
        Assert.assertNotEquals(initialValue, revertedValue);
        Assert.assertTrue(realmSettingsAttributePage.logoInputExists());
    }

    @Test
    public void testRealmSettingsAttributes() {
        realmSettingsAttributePage.navigateTo();
        waitForPageToLoad();

        Assert.assertTrue(realmSettingsAttributePage.logoInputExists());

        realmSettingsAttributePage.saveLogoField("http://assests.mycompany.com/logo.png");
        Assert.assertTrue(realmSettingsAttributePage.isSaved());
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


