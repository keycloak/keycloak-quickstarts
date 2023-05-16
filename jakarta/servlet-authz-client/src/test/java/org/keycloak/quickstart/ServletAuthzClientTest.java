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
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.test.page.LoginPage;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.keycloak.test.TestsHelper.deleteRealm;
import static org.keycloak.test.TestsHelper.importTestRealm;

/**
 * @author <a href="mailto:bruno@abstractj.org">Bruno Oliveira</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ServletAuthzClientTest {

    private static final String APP_NAME = "jakarta-servlet-authz-client";

    @Page
    private ClientPage indexPage;

    @Page
    private LoginPage loginPage;

    @Deployment(name = APP_NAME, order = 1, testable = false)
    public static Archive createTestArchive() throws IOException {
        return ShrinkWrap.create(ZipImporter.class, "jakarta-servlet-authz-client.war").importFrom(
                new File("target/jakarta-servlet-authz-client.war")).as(WebArchive.class);
    }

    @Drone
    private WebDriver webDriver;

    @ArquillianResource
    @OperateOnDeployment(APP_NAME)
    private URL contextRoot;

    @AfterClass
    public static void cleanUp() throws Exception {
        deleteRealm("admin", "admin", "quickstart");
    }

    @BeforeClass
    public static void onBeforeClass() {
        try {
            importTestRealm("admin", "admin", "/quickstart-realm.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Before
    public void onBefore() {
        webDriver.navigate().to(contextRoot);
    }

    @Test
    public void testAdminAccessToAdminResources() {
        try {
            loginPage.login("admin", "admin");
            indexPage.clickAdminLink();
            assertEquals("Should display the administrator page",
                    "Only Administrators can access this page.",
                    indexPage.getMessage());
            indexPage.clickLogout();
        } catch (Exception e) {
            debugTest(e);
            fail("Should display the main page");
        }
    }

    @Test
    public void testAdminGrantedPermissions() {
        try {

            loginPage.login("admin", "admin");
            assertTrue("Should display the admin resource permission",
                    webDriver.getPageSource().contains("Resource: Admin Resource"));
            assertTrue("Should display the admin scope permission",
                    webDriver.getPageSource().contains("urn:servlet-authz:protected:admin:access"));
            assertTrue("Should display the proteced resource permission",
                    webDriver.getPageSource().contains("Resource: Protected Resource"));
            assertTrue("Should display the protected resource scope permission",
                    webDriver.getPageSource().contains("urn:servlet-authz:protected:resource:access"));
            assertTrue("Should display the main page resource permission",
                    webDriver.getPageSource().contains("Resource: Main Page"));
            assertTrue("Should display the main page scope permission",
                    webDriver.getPageSource().contains("[urn:servlet-authz:page:main:actionForAdmin, urn:servlet-authz:page:main:actionForUser]"));
            assertFalse("Should NOT display the premium resource permission",
                    webDriver.getPageSource().contains("Resource: Premium Resource"));
            assertFalse("Should NOT display the premium resource scope permission",
                    webDriver.getPageSource().contains("urn:servlet-authz:protected:premium:access"));
            indexPage.clickLogout();
        } catch (Exception e) {
            debugTest(e);
            fail("Should display the main page");
        }
    }

    @Test
    public void testAliceGrantedPermissions() {
        try {

            loginPage.login("alice", "alice");
            assertTrue("Should display the proteced resource permission",
                    webDriver.getPageSource().contains("Resource: Protected Resource"));
            assertTrue("Should display the protected resource scope permission",
                    webDriver.getPageSource().contains("urn:servlet-authz:protected:resource:access"));
            assertTrue("Should display the main page resource permission",
                    webDriver.getPageSource().contains("Resource: Main Page"));
            assertTrue("Should display the main page user scope permission",
                    webDriver.getPageSource().contains("[urn:servlet-authz:page:main:actionForUser]"));
            assertFalse("Should NOT display the main page admin scope permission",
                    webDriver.getPageSource().contains("urn:servlet-authz:page:main:actionForAdmin"));
            assertFalse("Should NOT display the admin resource permission",
                    webDriver.getPageSource().contains("Resource: Admin Resource"));
            assertFalse("Should NOT display the admin scope permission",
                    webDriver.getPageSource().contains("urn:servlet-authz:protected:admin:access"));
            assertFalse("Should NOT display the premium resource permission",
                    webDriver.getPageSource().contains("Resource: Premium Resource"));
            assertFalse("Should NOT display the premium resource scope permission",
                    webDriver.getPageSource().contains("urn:servlet-authz:protected:premium:access"));
            indexPage.clickLogout();
        } catch (Exception e) {
            debugTest(e);
            fail("Should display the main page");
        }
    }

    @Test
    public void testJdoeGrantedPermissions() {
        try {

            loginPage.login("jdoe", "jdoe");
            assertTrue("Should display the proteced resource permission",
                    webDriver.getPageSource().contains("Resource: Protected Resource"));
            assertTrue("Should display the protected resource scope permission",
                    webDriver.getPageSource().contains("urn:servlet-authz:protected:resource:access"));
            assertTrue("Should display the premium resource permission",
                    webDriver.getPageSource().contains("Resource: Premium Resource"));
            assertTrue("Should display the premium resource scope permission",
                    webDriver.getPageSource().contains("urn:servlet-authz:protected:premium:access"));
            assertTrue("Should display the main page resource permission",
                    webDriver.getPageSource().contains("Resource: Main Page"));
            assertTrue("Should display the main page premium scope permission",
                    webDriver.getPageSource().contains("[urn:servlet-authz:page:main:actionForPremiumUser, urn:servlet-authz:page:main:actionForUser]"));
            assertFalse("Should NOT display the main page admin scope permission",
                    webDriver.getPageSource().contains("urn:servlet-authz:page:main:actionForAdmin"));
            assertFalse("Should NOT display the admin resource permission",
                    webDriver.getPageSource().contains("Resource: Admin Resource"));
            assertFalse("Should NOT display the admin scope permission",
                    webDriver.getPageSource().contains("urn:servlet-authz:protected:admin:access"));
            indexPage.clickLogout();
        } catch (Exception e) {
            debugTest(e);
            fail("Should display the main page");
        }
    }

    @Test
    public void testAdminAccessToPremiumResources() {
        try {

            loginPage.login("admin", "admin");
            indexPage.clickPremiumLink();
            assertEquals("Should display access denied page",
                    "You can not access this resource.",
                    indexPage.getMessage());
            indexPage.clickLogout();

        } catch (Exception e) {
            debugTest(e);
            fail("Should display the main page");
        }
    }

    @Test
    public void testAdminAccessToSharedResources() {
        try {
            loginPage.login("admin", "admin");
            indexPage.clickDynamicMenuLink();
            assertEquals("Should display the shared resource",
                    "Any authenticated user can access this page.",
                    indexPage.getMessage());
            indexPage.clickLogout();

        } catch (Exception e) {
            debugTest(e);
            fail("Should display the main page");
        }
    }

    @Test
    public void testPremiumAccessToPremiumResources() {
        try {

            loginPage.login("jdoe", "jdoe");
            indexPage.clickPremiumLink();
            assertEquals("Should display the premium page",
                    "Only for premium users.",
                    indexPage.getMessage());
            indexPage.clickLogout();

        } catch (Exception e) {
            debugTest(e);
            fail("Should display the main page");
        }
    }

    @Test
    public void testPremiumAccessToAdminResources() {
        try {

            loginPage.login("jdoe", "jdoe");
            indexPage.clickAdminLink();
            assertEquals("Should display access denied page",
                    "You can not access this resource.",
                    indexPage.getMessage());
            indexPage.clickLogout();

        } catch (Exception e) {
            debugTest(e);
            fail("Should display the main page");
        }
    }

    @Test
    public void testPremiumAccessToSharedResources() {
        try {

            loginPage.login("jdoe", "jdoe");
            indexPage.clickDynamicMenuLink();
            assertEquals("Should display the shared resource",
                    "Any authenticated user can access this page.",
                    indexPage.getMessage());
            indexPage.clickLogout();

        } catch (Exception e) {
            debugTest(e);
            fail("Should display the main page");
        }
    }

    @Test
    public void testUserAccessToAdminResources() {
        try {

            loginPage.login("alice", "alice");
            indexPage.clickAdminLink();
            assertEquals("Should display access denied page",
                    "You can not access this resource.",
                    indexPage.getMessage());
            indexPage.clickLogout();

        } catch (Exception e) {
            debugTest(e);
            fail("Should display the main page");
        }
    }

    @Test
    public void testUserAccessToPremiumResources() {
        try {

            loginPage.login("alice", "alice");
            indexPage.clickPremiumLink();
            assertEquals("Should display access denied page",
                    "You can not access this resource.",
                    indexPage.getMessage());
            indexPage.clickLogout();

        } catch (Exception e) {
            debugTest(e);
            fail("Should display the main page");
        }
    }

    @Test
    public void testUserAccessToSharedResources() {
        try {

            loginPage.login("alice", "alice");
            indexPage.clickDynamicMenuLink();
            assertEquals("Should display the shared resource",
                    "Any authenticated user can access this page.",
                    indexPage.getMessage());
            indexPage.clickLogout();

        } catch (Exception e) {
            debugTest(e);
            fail("Should display the main page");
        }
    }

    private void debugTest(Exception e) {
        System.out.println(webDriver.getPageSource());
        e.printStackTrace();
    }
}
