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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.test.page.IndexPage;
import org.keycloak.test.page.LoginPage;
import org.keycloak.test.TestsHelper;
import org.keycloak.test.builders.ClientBuilder;
import org.keycloak.test.page.ProfilePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.keycloak.test.TestsHelper.createClient;
import static org.keycloak.test.TestsHelper.deleteRealm;
import static org.keycloak.test.TestsHelper.importTestRealm;
import static org.keycloak.test.builders.ClientBuilder.AccessType.BEARER_ONLY;
import static org.keycloak.test.builders.ClientBuilder.AccessType.PUBLIC;

/**
 * @author <a href="mailto:bruno@abstractj.org">Bruno Oliveira</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ArquillianProfileJeeHtml5Test {

    private static final String WEBAPP_SRC = "src/main/webapp";
    private static final String APP_NAME = "app-profile-html5";
    private static final String APP_SERVICE = "service-jaxrs";
    private static final String ROOT_URL = "http://127.0.0.1:8080/app-profile-html5";

    @Page
    private IndexPage indexPage;

    @Page
    private LoginPage loginPage;

    @Page
    private ProfilePage profilePage;

    static {
        try {
            importTestRealm("admin", "admin", "/quickstart-realm.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deployment(name= APP_SERVICE, order = 1, testable = false)
    public static Archive<?> createTestArchive1() throws IOException {
        return ShrinkWrap.createFromZipFile(WebArchive.class,
                new File("../service-jee-jaxrs/target/service.war"))
                .addAsWebInfResource(
                        new StringAsset(createClient(
                                ClientBuilder.create(APP_SERVICE).accessType(BEARER_ONLY))), "keycloak.json");
    }

    @Deployment(name = APP_NAME, order = 2, testable = false)
    public static Archive<?> createTestArchive2() throws IOException {
        return ShrinkWrap.create(WebArchive.class, "app-profile-html5.war")
                .addAsWebResource(new File(WEBAPP_SRC, "app.js"))
                .addAsWebResource(new File(WEBAPP_SRC, "index.html"))
                .addAsWebResource(new File(WEBAPP_SRC, "keycloak.js"))
                .addAsWebResource(new File(WEBAPP_SRC, "styles.css"))
                .addAsWebResource(new StringAsset(createClient(ClientBuilder.create(APP_NAME)
                        .rootUrl(ROOT_URL)
                        .accessType(PUBLIC))), "keycloak.json");
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
            loginPage.login("test-admin", "password");
            assertTrue(Graphene.waitGui().until(ExpectedConditions.textToBePresentInElementLocated(By.id("username"), "admin")));
            profilePage.clickLogout();
        } catch (Exception e) {
            debugTest(e);
            fail("Should display logged in user");
        }
    }

    @Test
    public void testProfileMenu() {
        try {
            indexPage.clickLogin();
            loginPage.login("test-admin", "password");
            profilePage.clickToken();
            JsonObject json = parse(profilePage.getTokenContent());
            assertNotNull("JSON content should not be empty", json);
            assertEquals(json.get("aud").getAsString(), APP_NAME);
            assertFalse(json.get("session_state").isJsonNull());
            webDriver.navigate().to(contextRoot);
            
            waitTillElementIsClickable(By.name("logoutBtn"));
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
            loginPage.login("test-admin", "password");
            profilePage.clickAccount();
            assertEquals("Keycloak Account Management", webDriver.getTitle());
            webDriver.navigate().to(contextRoot);
            waitTillElementIsClickable(By.name("logoutBtn"));
            profilePage.clickLogout();
        } catch (Exception e) {
            debugTest(e);
            fail("Should display account management page");
        }
    }
    
    private void waitTillElementIsClickable(By locator) {
        Graphene.waitGui().withTimeout(30, TimeUnit.SECONDS).until(ExpectedConditions.elementToBeClickable(locator));
    }

    private JsonObject parse(String json) {
        return new JsonParser().parse(json).getAsJsonObject();
    }
    
    private void debugTest(Exception e) {
        System.out.println(webDriver.getPageSource());
        e.printStackTrace();
    }

}
