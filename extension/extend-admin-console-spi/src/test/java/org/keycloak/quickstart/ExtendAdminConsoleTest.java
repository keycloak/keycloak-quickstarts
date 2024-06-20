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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.test.FluentTestsHelper;
import org.keycloak.test.page.LoginPage;
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
    }

    @Test
    public void testAdminUiTodoApp() throws Exception {
        adminConsole.navigateTo();
        waitForPageToLoad();
        loginPage.login("admin", "admin");
        assertThat(webDriver.getTitle(), containsString("Keycloak Administration UI"));

        Assert.assertTrue(adminConsole.isTodoMenuPresent());
        adminConsole.clickTodoMenuItem();
        Assert.assertTrue(adminConsole.isOverviewPage());

        adminConsole.clickAddButton();
        adminConsole.fillTodoForm("something", "something that needs doing");
        adminConsole.clickSave();

        Assert.assertTrue(adminConsole.isSaved());
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
