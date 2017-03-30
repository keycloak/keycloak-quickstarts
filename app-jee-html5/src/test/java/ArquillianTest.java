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
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.helper.TestsHelper;
import org.keycloak.quickstart.page.IndexPage;
import org.keycloak.quickstart.page.LoginPage;
import org.keycloak.quickstart.util.ClientBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.keycloak.quickstart.page.IndexPage.MESSAGE_ADMIN;
import static org.keycloak.quickstart.page.IndexPage.MESSAGE_PUBLIC;
import static org.keycloak.quickstart.page.IndexPage.MESSAGE_SECURED;
import static org.keycloak.quickstart.page.IndexPage.UNAUTHORIZED;

/**
 * @author <a href="mailto:bruno@abstractj.org">Bruno Oliveira</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ArquillianTest {

    private static final String WEBAPP_SRC = "src/main/webapp";
    public static final String APP_NAME = "app-html5";

    @Page
    private IndexPage indexPage;

    @Page
    private LoginPage loginPage;

    @Deployment(name = "service-jee-jaxrs", order = 1, testable = false)
    public static Archive<?> createTestArchive1() throws IOException {
        return ShrinkWrap.createFromZipFile(WebArchive.class,
                new File("../service-jee-jaxrs/target/service.war"));
    }

    @Deployment(name = "app-html5", order = 2, testable = false)
    public static Archive<?> createTestArchive2() throws IOException {
        return ShrinkWrap.create(WebArchive.class, "app-html5.war")
                .addAsWebResource(new File(WEBAPP_SRC, "app.js"))
                .addAsWebResource(new File(WEBAPP_SRC, "index.html"))
                .addAsWebResource(new File(WEBAPP_SRC, "keycloak.js"))
                .addAsWebResource(new File(WEBAPP_SRC, "styles.css"))
                .addAsWebResource(new File("config", "keycloak.json"));

    }

    @Drone
    private WebDriver webDriver;

    @ArquillianResource
    @OperateOnDeployment("app-html5")
    private URL contextRoot;

    @Before
    public void setup() {
        webDriver.navigate().to(contextRoot);
    }

    @BeforeClass
    public static void init() throws IOException {
        TestsHelper.ImportTestRealm("admin","admin","/quickstart-realm.json");
        TestsHelper.createClient(ClientBuilder.create("service-jaxrs").bearerOnly(true));
        TestsHelper.createClient(ClientBuilder.create(APP_NAME)
                .rootUrl("http://127.0.0.1:8080/app-html5")
                .publicClient(true));
    }
    @AfterClass
    public static void cleanUp() throws IOException{
        TestsHelper.deleteRealm("admin","admin",TestsHelper.testRealm);
    }

    @Test
    public void testSecuredResource() throws InterruptedException {
        try {
            indexPage.clickSecured();
            assertTrue(Graphene.waitGui().until(ExpectedConditions.textToBePresentInElementLocated(By.className("error"), UNAUTHORIZED)));
        } catch (Exception e) {
            fail("Should display an error message");
        }
    }

    @Test
    public void testAdminResource() {
        try {
            indexPage.clickAdmin();
            assertTrue(Graphene.waitGui().until(ExpectedConditions.textToBePresentInElementLocated(By.className("error"), UNAUTHORIZED)));
        } catch (Exception e) {
            fail("Should display an error message");
        }
    }

    @Test
    public void testPublicResource() {
        try {
            indexPage.clickPublic();
            assertTrue(Graphene.waitGui().until(ExpectedConditions.textToBePresentInElementLocated(By.className("message"), MESSAGE_PUBLIC)));
        } catch (Exception e) {
            fail("Should display an error message");
        }
    }

    @Test
    public void testAdminWithAuthAndRole() throws MalformedURLException, InterruptedException {
        try {
            indexPage.clickLogin();
            loginPage.login("test-admin", "password");
            indexPage.clickAdmin();
            assertTrue(Graphene.waitGui().until(ExpectedConditions.textToBePresentInElementLocated(By.className("message"), MESSAGE_ADMIN)));
            indexPage.clickLogout();
        } catch (Exception e) {
            fail("Should display logged in user");
        }
    }

    @Test
    public void testUserWithAuthAndRole() throws MalformedURLException, InterruptedException {
        try {
            indexPage.clickLogin();
            loginPage.login("alice", "password");
            indexPage.clickSecured();
            assertTrue(Graphene.waitGui().until(ExpectedConditions.textToBePresentInElementLocated(By.className("message"), MESSAGE_SECURED)));
            indexPage.clickLogout();
        } catch (Exception e) {
            fail("Should display logged in user");
        }
    }
}
