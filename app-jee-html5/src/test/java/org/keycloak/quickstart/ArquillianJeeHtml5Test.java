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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.keycloak.test.TestsHelper;
import org.keycloak.test.builders.ClientBuilder;
import org.keycloak.test.page.IndexPage;
import org.keycloak.test.page.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.keycloak.test.TestsHelper.createClient;
import static org.keycloak.test.TestsHelper.deleteRealm;
import static org.keycloak.test.TestsHelper.importTestRealm;
import static org.keycloak.test.builders.ClientBuilder.AccessType.BEARER_ONLY;
import static org.keycloak.test.builders.ClientBuilder.AccessType.PUBLIC;
import static org.keycloak.test.page.IndexPage.UNAUTHORIZED;

/**
 * @author <a href="mailto:bruno@abstractj.org">Bruno Oliveira</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ArquillianJeeHtml5Test {

    private static final String WEBAPP_SRC = "src/main/webapp";
    private static final String APP_NAME = "app-html5";
    private static final String APP_SERVICE = "service-jaxrs";
    private static final String ROOT_URL = "http://127.0.0.1:8080/app-html5";

    @Page
    private IndexPage indexPage;

    @Page
    private LoginPage loginPage;

    @Drone
    WebDriver browser;

    static {
        try {
            importTestRealm("admin", "admin", "/quickstart-realm.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deployment(name = APP_SERVICE, order = 1, testable = false)
    public static Archive<?> createTestArchive1() {
        return ShrinkWrap.createFromZipFile(WebArchive.class,
                new File("../service-jee-jaxrs/target/service.war"))
                .addAsWebInfResource(
                        new StringAsset(createClient(
                                ClientBuilder.create(APP_SERVICE).accessType(BEARER_ONLY))), "keycloak.json");
    }

    @Deployment(name = APP_NAME, order = 2, testable = false)
    public static Archive<?> createTestArchive2() {
        return ShrinkWrap.create(WebArchive.class, "app-html5.war")
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
    public static void cleanUp() throws IOException {
        deleteRealm("admin", "admin", TestsHelper.testRealm);
    }

    @Before
    public void setup() {
        webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        webDriver.navigate().to(contextRoot);
        logoutIfLoggedIn();
        waitForAppToInitializeForLogin();
    }

    private void logoutIfLoggedIn() {
        List<WebElement> logoutBtn = browser.findElements(By.name("logoutBtn"));
        if (logoutBtn.size() > 0 && logoutBtn.get(0).isDisplayed()) {
            logout();
        }
    }

    private void waitForAppToInitializeForLogin() {
        Graphene.waitAjax().until(ExpectedConditions.elementToBeClickable(By.name("loginBtn")));
    }

    private void waitForAppToInitializeAfterLogin() {
        Graphene.waitAjax().until(ExpectedConditions.elementToBeClickable(By.name("logoutBtn")));
    }

    @Test
    public void testSecuredResource() {
        indexPage.clickSecured();
        assertTextInControl("error", UNAUTHORIZED);
    }

    @Test
    public void testAdminResource() {
        indexPage.clickAdmin();
        assertTextInControl("error", UNAUTHORIZED);
    }

    @Test
    public void testPublicResource() {
        indexPage.clickPublic();
        assertTextInControl("message", "Message: public");
    }

    @Test
    public void testAdminWithAuthAndRole() {
        indexPage.clickLogin();
        loginPage.login("test-admin", "password");
        waitForAppToInitializeAfterLogin();
        indexPage.clickAdmin();
        assertTextInControl("message", "Message: admin");
        logout();
    }

    @Test
    public void testUserWithAuthAndRole() {
        indexPage.clickLogin();
        loginPage.login("alice", "password");
        waitForAppToInitializeAfterLogin();
        indexPage.clickSecured();
        assertTextInControl("message", "Message: secured");
        logout();
    }

    private void logout() {
        indexPage.clickLogout();
        assertTextInControl("message", "");
    }

    @Rule
    public TestName name = new TestName();

    private void assertTextInControl(String control, String text) {
        try {
            assertTrue(Graphene.waitAjax().until(ExpectedConditions.textToBePresentInElementLocated(
                    By.className(control), text)));
        } catch (TimeoutException ex) {
            File screenshotAs = ((TakesScreenshot) browser).getScreenshotAs(OutputType.FILE);
            try {
                Path target = Paths.get("target", "surefire-reports", name.getMethodName() + ".png");
                Files.createDirectories(target.getParent());
                Files.copy(screenshotAs.toPath(), target);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            WebElement element = browser.findElement(By.className(control));
            if (element == null) {
                throw new TimeoutException("element not found", ex);
            } else {
                throw new TimeoutException("found text '" + element.getText() + "' when expecting text '" + text + "'");
            }
        }
    }

}
