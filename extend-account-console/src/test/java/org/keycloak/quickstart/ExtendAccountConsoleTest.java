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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.test.TestsHelper;
import org.keycloak.test.page.LoginPage;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;
import static org.keycloak.test.TestsHelper.importTestRealm;
import static org.keycloak.test.TestsHelper.deleteRealm;

/**
 * @author <a href="mailto:aboullos@redhat.com">Alfredo Moises Boullosa</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ExtendAccountConsoleTest {

    private static final String ADMIN_URL = "http://127.0.0.1:8180/auth/admin";

    @Page
    private LoginPage loginPage;

    @Page
    private ExtendedAccountPage accountPage;

    static {
        try {
            importTestRealm("admin", "admin", "/quickstart-realm.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Drone
    private WebDriver webDriver;

    @AfterClass
    public static void cleanUp() throws IOException {
        deleteRealm("admin", "admin", TestsHelper.testRealm);
    }

    @Before
    public void setup() {
        webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        webDriver.navigate().to(ADMIN_URL);
    }

    @Test
    public void keycloakManThemeTest() {
        try {
            accountPage.navigateTo();
            Assert.assertTrue(accountPage.isLogoPresent());

            accountPage.clickOverviewHome();

            Thread.sleep(2000);
            loginPage.login("test-admin", "password");
            Assert.assertTrue(accountPage.isOverviewPage());

            Thread.sleep(2000);
            accountPage.clickKeycloakManApp();
            Assert.assertTrue(accountPage.isKeycloakManPage());
        } catch (Exception e) {
            debugTest(e);
            fail("Keycloak-man theme is not set");
        }
    }
    
    private void debugTest(Exception e) {
        System.out.println(webDriver.getPageSource());
        e.printStackTrace();
    }
}
