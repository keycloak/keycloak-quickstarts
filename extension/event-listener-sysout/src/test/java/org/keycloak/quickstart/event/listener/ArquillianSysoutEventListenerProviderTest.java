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

package org.keycloak.quickstart.event.listener;

import org.hamcrest.Matchers;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.events.EventType;
import org.keycloak.representations.idm.RealmEventsConfigRepresentation;
import org.keycloak.test.page.LoginPage;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static org.keycloak.test.TestsHelper.deleteRealm;
import static org.keycloak.test.TestsHelper.importTestRealm;
import static org.keycloak.test.TestsHelper.keycloakBaseUrl;

/**
 * @author <a href="mailto:mkanis@redhat.com">Martin Kanis</a>
 */
@RunWith(Arquillian.class)
public class ArquillianSysoutEventListenerProviderTest {

    private static final Logger logger = Logger.getLogger(ArquillianSysoutEventListenerProviderTest.class);

    public static final String KEYCLOAK_URL = "http://localhost:8180";

    public static final String REALM_QS_EVENT_SYSOUT = "event-listener-sysout";

    public static final String KEYCLOAK_URL_CONSOLE = KEYCLOAK_URL + "/admin/%s/console/#%s";

    public static Keycloak ADMIN_CLIENT;

    public static String ADMIN_ID;

    @Page
    private LoginPage loginPage;

    @Drone
    private WebDriver webDriver;

    private LogReaderHelper logReader;


    @BeforeClass
    public static void setupClass() throws IOException {
        importTestRealm("admin", "admin", "/quickstart-realm.json");

        ADMIN_CLIENT = Keycloak.getInstance(keycloakBaseUrl, "master", "admin", "admin", "admin-cli");
        ADMIN_ID = ADMIN_CLIENT.realm(REALM_QS_EVENT_SYSOUT).users().search("test-admin").get(0).getId();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        deleteRealm("admin", "admin", REALM_QS_EVENT_SYSOUT);
    }

    @Before
    public void setup() throws IOException {
        webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        String basedir = System.getProperty("basedir");
        logger.infof("Basedir: %s", basedir);

        String logFileName = Paths.get(Paths.get(basedir).getParent().getParent().toString(), "keycloak.log").toString();
        logger.infof("Using log file %s", logFileName);

        logReader = new LogReaderHelper(logFileName);
        logReader.start();

        registerEventListener();
    }

    @After
    public void cleanup() {
        logReader.close();

        removeEventListener();
        logout();
    }

    @Test
    public void testEventListenerOutput() throws IOException, InterruptedException {
        logReader.clear();

        // generate some events
        loginToAdminConsole();
        assertUserEvent(EventType.LOGIN, false);
        assertUserEvent(EventType.CODE_TO_TOKEN, true);

        logout();
        assertAdminEvent("ACTION", false);

        loginToAdminConsole();
        assertUserEvent(EventType.LOGIN, false);
        assertUserEvent(EventType.CODE_TO_TOKEN, true);
    }


    private void registerEventListener() {
        RealmEventsConfigRepresentation realmEventsConfig = ADMIN_CLIENT.realm(REALM_QS_EVENT_SYSOUT).getRealmEventsConfig();
        realmEventsConfig.getEventsListeners().add("sysout");
        ADMIN_CLIENT.realm(REALM_QS_EVENT_SYSOUT).updateRealmEventsConfig(realmEventsConfig);
    }

    private void removeEventListener() {
        RealmEventsConfigRepresentation realmEventsConfig = ADMIN_CLIENT.realm(REALM_QS_EVENT_SYSOUT).getRealmEventsConfig();
        realmEventsConfig.getEventsListeners().remove("sysout");
        ADMIN_CLIENT.realm(REALM_QS_EVENT_SYSOUT).updateRealmEventsConfig(realmEventsConfig);
    }

    private void navigateToAdminConsole(String path) {
        webDriver.navigate().to(format(KEYCLOAK_URL_CONSOLE, REALM_QS_EVENT_SYSOUT, path));
    }

    private void loginToAdminConsole() throws InterruptedException {
        final String path = "/realms/" + REALM_QS_EVENT_SYSOUT + "/clients";

        navigateToAdminConsole(path);

        loginPage.login("test-admin", "password");

        // wait for URL to stop changing
        while (true) {
            String previousUrl = webDriver.getCurrentUrl();
            TimeUnit.SECONDS.sleep(1);
            if (webDriver.getCurrentUrl().equals(previousUrl)) {
                break;
            }
        }
    }

    private void logout() {
        ADMIN_CLIENT.realm(REALM_QS_EVENT_SYSOUT).users().get(ADMIN_ID).logout();
    }


    private void assertUserEvent(EventType expectedEventType, boolean expectExcluded) {
        String prefix;
        if (expectExcluded) {
            prefix = "USER EVENT EXCLUDED: ";
        } else {
            prefix = "USER EVENT: ";
        }

        String line = logReader.pollLine();
        if (line == null) {
            Assert.fail("Not line present in the server log when waiting for eventType " + expectedEventType);
        }

        if (expectExcluded) {
            Assert.assertEquals(line, prefix + expectedEventType);
        } else {
            Assert.assertThat(line, Matchers.containsString(prefix + "type=" + expectedEventType));
        }
    }

    private void assertAdminEvent(String expectedEventType, boolean expectExcluded) {
        String prefix;
        if (expectExcluded) {
            prefix = "ADMIN EVENT EXCLUDED: ";
        } else {
            prefix = "ADMIN EVENT: ";
        }

        String line = logReader.pollLine();
        if (line == null) {
            Assert.fail("Not line present in the server log when waiting for eventType " + expectedEventType);
        }

        if (expectExcluded) {
            Assert.assertEquals(line, prefix + expectedEventType);
        } else {
            Assert.assertThat(line, Matchers.containsString(prefix + "operationType=" + expectedEventType));
        }
    }
}