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

package org.keycloak.quickstart.event.storage;

import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import org.keycloak.admin.client.Keycloak;
import org.junit.runner.RunWith;

import org.keycloak.events.EventType;
import org.keycloak.representations.idm.AdminEventRepresentation;
import org.keycloak.representations.idm.RealmEventsConfigRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.quickstart.test.page.LoginPage;
import org.keycloak.quickstart.test.FluentTestsHelper;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * @author <a href="mailto:mkanis@redhat.com">Martin Kanis</a>
 */
@RunWith(Arquillian.class)
public class ArquillianEventStoreMemoryProviderTest {

    public static final String REALM_QS_EVENT_STORE = "event-store-mem";

    public static final String KEYCLOAK_URL = "http://localhost:8180";

    public static final String KEYCLOAK_URL_CONSOLE = KEYCLOAK_URL + "/admin/%s/console/#%s";

    public static String ADMIN_ID;

    private static FluentTestsHelper fluentTestsHelper;

    @Page
    private LoginPage loginPage;

    @Drone
    private WebDriver webDriver;

    @BeforeClass
    public static void setupClass() throws IOException {
        fluentTestsHelper = new FluentTestsHelper(KEYCLOAK_URL,
                FluentTestsHelper.DEFAULT_ADMIN_USERNAME,
                FluentTestsHelper.DEFAULT_ADMIN_PASSWORD,
                FluentTestsHelper.DEFAULT_ADMIN_REALM,
                FluentTestsHelper.DEFAULT_ADMIN_CLIENT,
                REALM_QS_EVENT_STORE)
                .init("/quickstart-realm.json");
        ADMIN_ID = fluentTestsHelper.getKeycloakInstance().realm(REALM_QS_EVENT_STORE).users().search("test-admin").get(0).getId();
    }

    @AfterClass
    public static void tearDownClass() {
        fluentTestsHelper.deleteRealm(REALM_QS_EVENT_STORE);
    }

    @Before
    public void init() {
        webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        enableEventsSettings();
    }

    @Test
    @RunAsClient
    public void testIfEventsAreShowed() throws InterruptedException {
        // Clear events
        clearEvents();

        // logout and login to generate some events
        logout();
        loginToAdminConsole();

        checkIfEventExists(EventType.CODE_TO_TOKEN.name(), EventType.LOGIN.name());

        // add an user to generate admin event
        addUser();
        checkAdminEvents("CREATE", "USER");

        // clear events and check if the events are gone (they should because they shouldn't be persisted in the DB)
        clearEvents();
        Assert.assertTrue(fluentTestsHelper.getKeycloakInstance().realm(REALM_QS_EVENT_STORE).getEvents().isEmpty());
        Assert.assertTrue(fluentTestsHelper.getKeycloakInstance().realm(REALM_QS_EVENT_STORE).getAdminEvents().isEmpty());
    }

    @After
    public void cleanup() {
        disableEventsSettings();
    }

    private void navigateToAdminConsole(String path) {
        webDriver.navigate().to(format(KEYCLOAK_URL_CONSOLE, REALM_QS_EVENT_STORE, path));
    }

    private void enableEventsSettings() {
        RealmEventsConfigRepresentation realmEventsConfig = fluentTestsHelper.getKeycloakInstance().realm(REALM_QS_EVENT_STORE).getRealmEventsConfig();
        realmEventsConfig.setEventsEnabled(true);
        realmEventsConfig.setAdminEventsEnabled(true);
        fluentTestsHelper.getKeycloakInstance().realm(REALM_QS_EVENT_STORE).updateRealmEventsConfig(realmEventsConfig);

        clearEvents();
    }

    private void clearEvents() {
        fluentTestsHelper.getKeycloakInstance().realm(REALM_QS_EVENT_STORE).clearEvents();
        fluentTestsHelper.getKeycloakInstance().realm(REALM_QS_EVENT_STORE).clearAdminEvents();
    }

    private void disableEventsSettings() {
        RealmEventsConfigRepresentation realmEventsConfig = fluentTestsHelper.getKeycloakInstance().realm(REALM_QS_EVENT_STORE).getRealmEventsConfig();
        realmEventsConfig.setEventsEnabled(false);
        realmEventsConfig.setAdminEventsEnabled(false);
        fluentTestsHelper.getKeycloakInstance().realm(REALM_QS_EVENT_STORE).updateRealmEventsConfig(realmEventsConfig);
    }

    private void loginToAdminConsole() throws InterruptedException {
        final String path = "/realms/" + REALM_QS_EVENT_STORE + "/clients";

        navigateToAdminConsole(path);

        loginPage.login("test-admin", fluentTestsHelper.changePassword("test-admin", REALM_QS_EVENT_STORE));

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
        fluentTestsHelper.getKeycloakInstance().realm(REALM_QS_EVENT_STORE).users().get(ADMIN_ID).logout();
    }

    private void checkIfEventExists(String... events) {
        List<String> actualEvents = fluentTestsHelper.getKeycloakInstance().realm(REALM_QS_EVENT_STORE).getEvents().stream().map(e -> e.getType()).collect(Collectors.toList());

        Assert.assertThat(actualEvents, Matchers.hasItems(events));
    }

    private void checkAdminEvents(String operationType, String resourceType) {
        List<AdminEventRepresentation> adminEvents = fluentTestsHelper.getKeycloakInstance().realm(REALM_QS_EVENT_STORE).getAdminEvents();
        List<String> operationTypes = adminEvents.stream().map(e -> e.getOperationType()).collect(Collectors.toList());
        List<String> resourceTypes = adminEvents.stream().map(e -> e.getResourceType()).collect(Collectors.toList());

        Assert.assertThat(operationTypes, Matchers.hasItem(operationType));
        Assert.assertThat(resourceTypes, Matchers.hasItem(resourceType));
    }

    private void addUser() {
        UserRepresentation user = new UserRepresentation();
        user.setUsername("test-user");
        fluentTestsHelper.getKeycloakInstance().realm(REALM_QS_EVENT_STORE).users().create(user);
    }
}