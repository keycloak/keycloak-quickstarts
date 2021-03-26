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
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.events.EventType;
import org.keycloak.representations.idm.RealmEventsConfigRepresentation;
import org.keycloak.test.page.LoginPage;
import org.openqa.selenium.WebDriver;
import org.wildfly.extras.creaper.core.ManagementClient;
import org.wildfly.extras.creaper.core.online.CliException;
import org.wildfly.extras.creaper.core.online.ManagementProtocol;
import org.wildfly.extras.creaper.core.online.ModelNodeResult;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.OnlineOptions;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
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

    public static final String PROVIDER_JAR = "event-listener-sysout";

    public static final String RESOURCES_SRC = "src/test/resources";

    public static final String REALM_QS_EVENT_SYSOUT = "event-listener-sysout";

    public static final String KEYCLOAK_URL_CONSOLE = "http://%s:%s/auth/admin/%s/console/#%s";

    public static Keycloak ADMIN_CLIENT;

    public static int keycloakManagementPort;

    public static String ADMIN_ID;

    private OnlineManagementClient onlineManagementClient;

    @Page
    private LoginPage loginPage;

    @Drone
    private WebDriver webDriver;

    @ArquillianResource
    @OperateOnDeployment(PROVIDER_JAR)
    public static URL keycloakContextRoot;

    @Deployment(testable=false, name=PROVIDER_JAR)
    @TargetsContainer("keycloak-remote")
    public static Archive<?> createProviderArchive() {
        return ShrinkWrap.create(JavaArchive.class, PROVIDER_JAR + ".jar")
                .addClasses(
                        SysoutEventListenerProvider.class,
                        SysoutEventListenerProviderFactory.class)
                .addAsManifestResource(new File(RESOURCES_SRC, "MANIFEST.MF"))
                .addAsServiceProvider(EventListenerProviderFactory.class, SysoutEventListenerProviderFactory.class);
    }

    @BeforeClass
    public static void setupClass() throws IOException {
        importTestRealm("admin", "admin", "/quickstart-realm.json");

        keycloakManagementPort = Integer.parseInt(System.getProperty("keycloakManagementPort"));

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

        registerEventListener();

        onlineManagementClient = ManagementClient.online(OnlineOptions
                .standalone()
                .hostAndPort(keycloakContextRoot.getHost(), keycloakManagementPort)
                .protocol(ManagementProtocol.HTTP_REMOTING)
                .build()
        );
    }

    @After
    public void cleanup() {
        removeEventListener();
        logout();
    }

    @Test
    public void testEventListenerOutput() throws IOException, CliException, InterruptedException {
        // generate some events
        loginToAdminConsole();
        logout();
        loginToAdminConsole();

        // check all events in the server's log
        String log = getServerLog();
        checkServerLogForEvents(log, EventType.LOGIN, EventType.CODE_TO_TOKEN);
    }

    @Test
    public void testEventListenerExcludes() throws IOException, CliException, InterruptedException {
        addExcludes();

        try {
            // generate some events
            loginToAdminConsole();
            logout();
            loginToAdminConsole();

            // check all expected events in the server's log
            String log = getServerLog();
            checkServerLogForEvents(log, EventType.LOGIN);

            // check if CODE_TO_TOKEN event was filtered out
            Assert.assertThat(log, Matchers.not(Matchers.containsString(EventType.CODE_TO_TOKEN.name())));
        }
        finally {
            removeSpi();
        }
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
        webDriver.navigate().to(format(KEYCLOAK_URL_CONSOLE, keycloakContextRoot.getHost(),
                keycloakContextRoot.getPort(), REALM_QS_EVENT_SYSOUT, path));
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

    /**
     * Returns last 10 lines (default) from the server log.
     * @return String last 10 lines of the log as JSON string
     */
    private String getServerLog() throws IOException, CliException {
        ModelNodeResult result = onlineManagementClient.execute("/subsystem=logging/log-file=server.log:read-log-file");
        result.assertSuccess();
        return result.get("result").toJSONString(false);
    }

    private void addExcludes() throws IOException, CliException {
        ModelNodeResult result = onlineManagementClient.execute("/subsystem=keycloak-server/spi=eventsListener/provider=sysout/:add(properties={exclude-events => \"[\\\"REFRESH_TOKEN\\\",\\\"CODE_TO_TOKEN\\\"]\"},enabled=true)");
        result.assertSuccess();
        onlineManagementClient.executeCli("reload");

        // reconnect admin client
        ADMIN_CLIENT = Keycloak.getInstance(keycloakBaseUrl, "master", "admin", "admin", "admin-cli");
    }

    private void removeSpi() throws IOException, CliException {
        ModelNodeResult result = onlineManagementClient.execute("/subsystem=keycloak-server/spi=eventsListener/:remove");
        result.assertSuccess();
        onlineManagementClient.executeCli("reload");

        // reconnect admin client
        ADMIN_CLIENT = Keycloak.getInstance(keycloakBaseUrl, "master", "admin", "admin", "admin-cli");
    }

    private void checkServerLogForEvents(String log, EventType... events) {
        Arrays.stream(events).forEach(event -> Assert.assertThat(log, Matchers.containsString(event.name())));
    }
}