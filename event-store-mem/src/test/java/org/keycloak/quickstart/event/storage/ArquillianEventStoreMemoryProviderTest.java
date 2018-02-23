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
import org.jboss.arquillian.container.test.api.*;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.junit.*;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.events.EventStoreProviderFactory;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.wildfly.extras.creaper.core.ManagementClient;
import org.wildfly.extras.creaper.core.online.*;
import org.junit.runner.RunWith;

import org.keycloak.events.EventType;
import org.keycloak.representations.idm.AdminEventRepresentation;
import org.keycloak.representations.idm.RealmEventsConfigRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.test.page.LoginPage;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.keycloak.test.TestsHelper.deleteRealm;
import static org.keycloak.test.TestsHelper.importTestRealm;
import static org.keycloak.test.TestsHelper.keycloakBaseUrl;

/**
 * @author <a href="mailto:mkanis@redhat.com">Martin Kanis</a>
 */
@RunWith(Arquillian.class)
public class ArquillianEventStoreMemoryProviderTest {

    public static final String PROVIDER_JAR = "event-store-mem";

    public static final String RESOURCES_SRC = "src/test/resources";

    public static final String REALM_QS_EVENT_STORE = "event-store-mem";

    public static final String KEYCLOAK_URL_CONSOLE = "http://%s:%s/auth/admin/%s/console/#%s";

    public static Keycloak ADMIN_CLIENT;

    public static String ADMIN_ID;

    public static int keycloakManagementPort;

    public static OnlineManagementClient onlineManagementClient;

    @Page
    private LoginPage loginPage;

    @Drone
    private WebDriver webDriver;

    @ArquillianResource
    private Deployer deployer;

    public static URL keycloakContextRoot;

    @Deployment(managed = false, testable=false, name=PROVIDER_JAR)
    @TargetsContainer("keycloak-remote")
    public static Archive<?> createProviderArchive() {
        return ShrinkWrap.create(JavaArchive.class, PROVIDER_JAR + ".jar")
                .addClasses(
                        MemEventStoreProvider.class,
                        MemEventStoreProviderFactory.class,
                        MemEventQuery.class,
                        MemAdminEventQuery.class)
                .addAsManifestResource(new File(RESOURCES_SRC, "MANIFEST.MF"))
                .addAsServiceProvider(EventStoreProviderFactory.class, MemEventStoreProviderFactory.class);
    }

    @BeforeClass
    public static void setupClass() throws IOException {
        importTestRealm("admin", "admin", "/quickstart-realm.json");

        keycloakManagementPort = Integer.parseInt(System.getProperty("keycloakManagementPort"));

        ADMIN_CLIENT = Keycloak.getInstance(keycloakBaseUrl, "master", "admin", "admin", "admin-cli");
        ADMIN_ID = ADMIN_CLIENT.realm(REALM_QS_EVENT_STORE).users().search("test-admin").get(0).getId();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        deleteRealm("admin", "admin", REALM_QS_EVENT_STORE);
    }

    @InSequence(1)
    @Test
    @RunAsClient
    public void deploy() {
        deployer.deploy(PROVIDER_JAR);
    }

    @InSequence(2)
    @Test
    @RunAsClient
    public void init(@ArquillianResource URL url) throws IOException, CliException {
        webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        keycloakContextRoot = url;

        onlineManagementClient = ManagementClient.online(OnlineOptions
                .standalone()
                .hostAndPort(keycloakContextRoot.getHost(), keycloakManagementPort)
                .protocol(ManagementProtocol.HTTP_REMOTING)
                .build()
        );

        addDefaultProvider();
        enableEventsSettings();
    }

    @InSequence(3)
    @Test
    @RunAsClient
    public void testIfEventsAreShowed() throws InterruptedException, IOException, CliException {
        // logout and login to generate some events
        logout();
        navigateAndLogin("/realms/" + REALM_QS_EVENT_STORE);

        checkIfEventExists(EventType.CODE_TO_TOKEN.name(), EventType.LOGIN.name());

        // add an user to generate admin event
        addUser();
        checkAdminEvents("CREATE", "USER");

        // restart and check if the events are gone (they should because they shouldn't be persisted in the DB)
        restartRemoteServer();
        Assert.assertTrue(ADMIN_CLIENT.realm(REALM_QS_EVENT_STORE).getEvents().isEmpty());
        Assert.assertTrue(ADMIN_CLIENT.realm(REALM_QS_EVENT_STORE).getAdminEvents().isEmpty());
    }

    @InSequence(4)
    @Test
    @RunAsClient
    public void cleanup() throws IOException, CliException {
        removeDefaultProvider();
        disableEventsSettings();
        deployer.undeploy(PROVIDER_JAR);
        restartRemoteServer();
    }

    private void navigateTo(String path) {
        webDriver.navigate().to(format(KEYCLOAK_URL_CONSOLE, keycloakContextRoot.getHost(),
                keycloakContextRoot.getPort(), REALM_QS_EVENT_STORE, path));
    }

    private void enableEventsSettings() {
        RealmEventsConfigRepresentation realmEventsConfig = ADMIN_CLIENT.realm(REALM_QS_EVENT_STORE).getRealmEventsConfig();
        realmEventsConfig.setEventsEnabled(true);
        realmEventsConfig.setAdminEventsEnabled(true);
        ADMIN_CLIENT.realm(REALM_QS_EVENT_STORE).updateRealmEventsConfig(realmEventsConfig);

        ADMIN_CLIENT.realm(REALM_QS_EVENT_STORE).clearEvents();
        ADMIN_CLIENT.realm(REALM_QS_EVENT_STORE).clearAdminEvents();
    }

    private void disableEventsSettings() {
        RealmEventsConfigRepresentation realmEventsConfig = ADMIN_CLIENT.realm(REALM_QS_EVENT_STORE).getRealmEventsConfig();
        realmEventsConfig.setEventsEnabled(false);
        realmEventsConfig.setAdminEventsEnabled(false);
        ADMIN_CLIENT.realm(REALM_QS_EVENT_STORE).updateRealmEventsConfig(realmEventsConfig);
    }

    /**
     * Navigates to provided path and login. Waits for redirects.
     * @param path String path to navigate to
     */
    private void navigateAndLogin(String path) throws InterruptedException {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        navigateTo(path);
        String currentUrl = webDriver.getCurrentUrl();

        // do we need to login?
        if (!currentUrl.endsWith(path) && !currentUrl.contains(path + "&state=")) {
            // wait for redirect to login page
            while (!currentUrl.contains("protocol/openid-connect/auth?client_id=security-admin-console")) {
                TimeUnit.MILLISECONDS.sleep(50);
                currentUrl = webDriver.getCurrentUrl();
            }

            loginPage.login("test-admin", "password");
            currentUrl = webDriver.getCurrentUrl();

            // wait for redirect to original page
            while (!currentUrl.contains(path)) {
                TimeUnit.MILLISECONDS.sleep(50);
                currentUrl = webDriver.getCurrentUrl();
            }
        }
    }

    private void logout() throws InterruptedException {
        ADMIN_CLIENT.realm(REALM_QS_EVENT_STORE).users().get(ADMIN_ID).logout();

        navigateTo("");
        String currentUrl = webDriver.getCurrentUrl();

        while (!currentUrl.contains("protocol/openid-connect/auth?client_id=security-admin-console")) {
            TimeUnit.MILLISECONDS.sleep(50);
            currentUrl = webDriver.getCurrentUrl();
        }
    }

    private void addDefaultProvider() throws IOException, CliException {
        ModelNodeResult result = onlineManagementClient.execute("/subsystem=keycloak-server/spi=eventsStore:write-attribute(name=default-provider,value=in-mem)");
        result.assertSuccess();
        restartRemoteServer();
    }

    private void removeDefaultProvider() throws IOException, CliException {
        ModelNodeResult result = onlineManagementClient.execute("/subsystem=keycloak-server/spi=eventsStore:undefine-attribute(name=default-provider)");
        result.assertSuccess();
    }

    private void checkIfEventExists(String... events) {
        List<String> actualEvents = ADMIN_CLIENT.realm(REALM_QS_EVENT_STORE).getEvents().stream().map(e -> e.getType()).collect(Collectors.toList());

        Assert.assertThat(actualEvents, Matchers.hasItems(events));
    }

    private void checkAdminEvents(String operationType, String resourceType) {
        List<AdminEventRepresentation> adminEvents = ADMIN_CLIENT.realm(REALM_QS_EVENT_STORE).getAdminEvents();
        List<String> operationTypes = adminEvents.stream().map(e -> e.getOperationType()).collect(Collectors.toList());
        List<String> resourceTypes = adminEvents.stream().map(e -> e.getResourceType()).collect(Collectors.toList());

        Assert.assertThat(operationTypes, Matchers.hasItem(operationType));
        Assert.assertThat(resourceTypes, Matchers.hasItem(resourceType));
    }

    private void addUser() {
        UserRepresentation user = new UserRepresentation();
        user.setUsername("test-user");
        ADMIN_CLIENT.realm(REALM_QS_EVENT_STORE).users().create(user);
    }

    private void restartRemoteServer() throws IOException, CliException {
        onlineManagementClient.executeCli("reload");
        ADMIN_CLIENT = Keycloak.getInstance(keycloakBaseUrl, "master", "admin", "admin", "admin-cli");
    }
}