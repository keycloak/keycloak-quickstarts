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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.test.page.IndexPage;
import org.keycloak.test.page.LoginPage;
import org.keycloak.test.page.ProfilePage;
import org.keycloak.quickstart.profilejee.Controller;
import org.keycloak.test.TestsHelper;
import org.keycloak.test.builders.ClientBuilder;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.keycloak.test.TestsHelper.createClient;
import static org.keycloak.test.TestsHelper.deleteRealm;
import static org.keycloak.test.TestsHelper.importTestRealm;
import static org.keycloak.test.builders.ClientBuilder.AccessType.BEARER_ONLY;

/**
 * @author <a href="mailto:bruno@abstractj.org">Bruno Oliveira</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ArquillianProfileSamlJeeJspTest {
    private final static Logger log = Logger.getLogger(ArquillianProfileSamlJeeJspTest.class);

    private static final String WEBAPP_SRC = "src/main/webapp";
    private static final String APP_NAME = "app-profile-saml";
    private static final String APP_SERVICE = "service-jaxrs";

    @Page
    private IndexPage indexPage;

    @Page
    private PageHelper pageHelper;

    @Page
    private LoginPage loginPage;

    @Page
    private ProfilePage profilePage;

    static {
        try {
            importTestRealm("admin", "admin", "/quickstart-realm.json");
        } catch (Exception e) {
            // print stacktrace here as an exception in a static initializer will lead to a class initialization problem
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Deployment(name = APP_SERVICE, order = 1, testable = false)
    public static Archive<?> createTestArchive1() throws IOException {
        return ShrinkWrap.createFromZipFile(WebArchive.class,
                new File("../service-jee-jaxrs/target/service.war"))
                .addAsWebInfResource(
                        new StringAsset(createClient(
                                ClientBuilder.create(APP_SERVICE).accessType(BEARER_ONLY))), "keycloak.json");
    }

    @Deployment(name = APP_NAME, order = 2, testable = false)
    public static Archive<?> createTestArchive2() throws IOException {
        return ShrinkWrap.create(WebArchive.class, "app-profile-saml.war")
                .addPackages(true, Filters.exclude(".*Test.*"), Controller.class.getPackage())
                .addAsWebResource(new File(WEBAPP_SRC, "index.jsp"))
                .addAsWebResource(new File(WEBAPP_SRC, "profile.jsp"))
                .addAsWebResource(new File(WEBAPP_SRC, "styles.css"))
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new File("src/test/resources", "keycloak-saml.xml"))
                .setWebXML(new File("src/main/webapp", "WEB-INF/web.xml"));
    }

    @Drone
    private WebDriver webDriver;

    @ArquillianResource
    @OperateOnDeployment(APP_NAME)
    private URL contextRoot;

    @AfterClass
    public static void cleanUp() throws IOException{
        deleteRealm("admin","admin",TestsHelper.testRealm);
    }

    @Before
    public void setup() {
        webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        webDriver.navigate().to(contextRoot);
    }

    @Test
    public void testLogin() throws InterruptedException {
        try {
            indexPage.clickLogin();
            loginPage.login("alice", "password");
            // due to https://issues.redhat.com/browse/KEYCLOAK-14103 a second click to login is required
            // need to upgrade to Wildfly 19.1.0 to support a solution to this
            if (pageHelper.isOnStartPage()) {
                log.info("found myself on the login page after login, clicking login again");
                indexPage.clickLogin();
            }
            pageHelper.waitForAccountBtn();
            assertEquals(profilePage.getUsername(), "alice");
            profilePage.clickLogout();
        } catch (Exception e) {
            debugTest(e);
            fail("Should display logged in user");
        }
    }

    @Test
    public void testAccessAccountManagement() {
        try {
            indexPage.clickLogin();
            loginPage.login("alice", "password");
            // due to https://issues.redhat.com/browse/KEYCLOAK-14103 a second click to login is required
            // need to upgrade to Wildfly 19.1.0 to support a solution to this
            if (pageHelper.isOnStartPage()) {
                log.info("found myself on the login page after login, clicking login again");
                indexPage.clickLogin();
            }
            pageHelper.waitForAccountBtn();
            profilePage.clickAccount();
            assertTrue(webDriver.getTitle().contains("Account Management"));
        } catch (Exception e) {
            debugTest(e);
            fail("Should display account management page");
        }
    }

    private void debugTest(Exception e) {
        System.out.println(webDriver.getPageSource());
        e.printStackTrace();
    }
}
