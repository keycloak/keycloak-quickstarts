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
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
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
import org.keycloak.test.FluentTestsHelper;
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
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

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

    private static OnlineManagementClient onlineManagementClient;

    public static final String KEYCLOAK_URL = "http://localhost:8180/auth";

    public static final String KEYCLOAK_HOST = URI.create(KEYCLOAK_URL).getHost();
    public static final int KEYCLOAK_PORT = URI.create(KEYCLOAK_URL).getPort();
    public static final int KEYCLOAK_MANAGEMENT_PORT = 10090;

    public static final FluentTestsHelper testHelper = new FluentTestsHelper(KEYCLOAK_URL,
            FluentTestsHelper.DEFAULT_ADMIN_USERNAME,
            FluentTestsHelper.DEFAULT_ADMIN_PASSWORD,
            FluentTestsHelper.DEFAULT_ADMIN_REALM,
            FluentTestsHelper.DEFAULT_ADMIN_CLIENT,
            FluentTestsHelper.DEFAULT_TEST_REALM);

    @Page
    private LoginPage loginPage;

    @Drone
    private WebDriver webDriver;

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
    public static void setupClass() throws Exception {
        onlineManagementClient = ManagementClient.online(OnlineOptions
                .standalone()
                .hostAndPort(KEYCLOAK_HOST, KEYCLOAK_MANAGEMENT_PORT)
                .protocol(ManagementProtocol.HTTP_REMOTING)
                .build()
        );

        testHelper.init();
        testHelper.importTestRealm("/quickstart-realm.json");

        ADMIN_CLIENT = Keycloak.getInstance(KEYCLOAK_URL, FluentTestsHelper.DEFAULT_ADMIN_REALM, FluentTestsHelper.DEFAULT_ADMIN_USERNAME, FluentTestsHelper.DEFAULT_ADMIN_PASSWORD, FluentTestsHelper.DEFAULT_ADMIN_CLIENT);
        ADMIN_ID = ADMIN_CLIENT.realm(REALM_QS_EVENT_SYSOUT).users().search("test-admin").get(0).getId();
    }

    @AfterClass
    public static void tearDownClass() {
        testHelper
                // We manually logout users, so here we need to re-initialize
                .init()
                .deleteRealm(REALM_QS_EVENT_SYSOUT);
    }

    @Before
    public void setup() throws IOException {
        webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        registerEventListener();
    }

    @After
    public void cleanup() throws Exception {
        removeEventListener();
        logout();
        removeSpi();
    }

    @Test
    public void testEventListenerOutput() throws IOException, CliException, InterruptedException {
        addDefaultProvider();
        // generate some events
        navigateAndLogin("/realms/" + REALM_QS_EVENT_SYSOUT + "/clients");
        logout();
        navigateAndLogin("/realms/" + REALM_QS_EVENT_SYSOUT + "/clients");

        // check all events in the server's log
        String log = getServerLog();
        checkServerLogForEvents(log, EventType.LOGIN);
    }

    @Test
    public void testEventListenerExcludes() throws IOException, CliException, InterruptedException {
        addExcludes();
        // generate some events
        navigateAndLogin("/realms/" + REALM_QS_EVENT_SYSOUT);
        logout();
        navigateAndLogin("/realms/" + REALM_QS_EVENT_SYSOUT);

        // check all expected events in the server's log
        String log = getServerLog();
        Assert.assertThat(log, Matchers.not(Matchers.containsString(EventType.LOGIN.name())));
    }

    private void registerEventListener() {
        RealmEventsConfigRepresentation realmEventsConfig = ADMIN_CLIENT.realm(REALM_QS_EVENT_SYSOUT).getRealmEventsConfig();
        realmEventsConfig.setEventsListeners(new LinkedList<>());
        realmEventsConfig.getEventsListeners().add("sysout");
        ADMIN_CLIENT.realm(REALM_QS_EVENT_SYSOUT).updateRealmEventsConfig(realmEventsConfig);
    }

    private void removeEventListener() {
        RealmEventsConfigRepresentation realmEventsConfig = ADMIN_CLIENT.realm(REALM_QS_EVENT_SYSOUT).getRealmEventsConfig();
        realmEventsConfig.setEventsListeners(new LinkedList<>());
        realmEventsConfig.getEventsListeners().remove("sysout");
        ADMIN_CLIENT.realm(REALM_QS_EVENT_SYSOUT).updateRealmEventsConfig(realmEventsConfig);
    }

    private void navigateTo(String path) {
        webDriver.navigate().to(format(KEYCLOAK_URL_CONSOLE, KEYCLOAK_HOST,
                KEYCLOAK_PORT, REALM_QS_EVENT_SYSOUT, path));
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
                TimeUnit.MILLISECONDS.sleep(100);
                currentUrl = webDriver.getCurrentUrl();
            }

            loginPage.login("test-admin", "password");
            currentUrl = webDriver.getCurrentUrl();

            // wait for redirect to original page
            while (!currentUrl.contains(path)) {
                TimeUnit.MILLISECONDS.sleep(100);
                currentUrl = webDriver.getCurrentUrl();
            }
        }
    }

    private void logout() throws InterruptedException {
        navigateTo("");
        TimeUnit.MILLISECONDS.sleep(100);
        webDriver.manage().deleteAllCookies();

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

    private static void addDefaultProvider() throws IOException, CliException {
        ModelNodeResult result = onlineManagementClient.execute("/subsystem=keycloak-server/spi=eventsListener:add(default-provider=sysout)");
        result.assertSuccess();
        result = onlineManagementClient.execute("/subsystem=keycloak-server/spi=eventsListener/provider=sysout:add(enabled=true)");
        result.assertSuccess();
        onlineManagementClient.executeCli("reload");

        // reconnect admin client
        ADMIN_CLIENT = Keycloak.getInstance(KEYCLOAK_URL, FluentTestsHelper.DEFAULT_ADMIN_REALM, FluentTestsHelper.DEFAULT_ADMIN_USERNAME, FluentTestsHelper.DEFAULT_ADMIN_PASSWORD, FluentTestsHelper.DEFAULT_ADMIN_CLIENT);
    }

    private void addExcludes() throws IOException, CliException {
        ModelNodeResult result = onlineManagementClient.execute("/subsystem=keycloak-server/spi=eventsListener:add(default-provider=sysout)");
        result.assertSuccess();
        result = onlineManagementClient.execute("/subsystem=keycloak-server/spi=eventsListener/provider=sysout/:add(properties={exclude-events => \"[\\\"LOGIN\\\"]\"},enabled=true)");
        result.assertSuccess();
        onlineManagementClient.executeCli("reload");

        // reconnect admin client
        ADMIN_CLIENT = Keycloak.getInstance(KEYCLOAK_URL, FluentTestsHelper.DEFAULT_ADMIN_REALM, FluentTestsHelper.DEFAULT_ADMIN_USERNAME, FluentTestsHelper.DEFAULT_ADMIN_PASSWORD, FluentTestsHelper.DEFAULT_ADMIN_CLIENT);
    }

    private static void removeSpi() throws IOException, CliException {
        onlineManagementClient.execute("/subsystem=keycloak-server/spi=eventsListener/:remove");
        onlineManagementClient.executeCli("reload");

        // reconnect admin client
        ADMIN_CLIENT = Keycloak.getInstance(KEYCLOAK_URL, FluentTestsHelper.DEFAULT_ADMIN_REALM, FluentTestsHelper.DEFAULT_ADMIN_USERNAME, FluentTestsHelper.DEFAULT_ADMIN_PASSWORD, FluentTestsHelper.DEFAULT_ADMIN_CLIENT);
    }

    private void checkServerLogForEvents(String log, EventType... events) {
        Arrays.stream(events).forEach(event -> Assert.assertThat(log, Matchers.containsString(event.name())));
    }
}