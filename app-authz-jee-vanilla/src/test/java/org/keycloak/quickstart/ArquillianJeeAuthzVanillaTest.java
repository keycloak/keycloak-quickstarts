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
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.test.page.LoginPage;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.keycloak.quickstart.page.AuthzPage;
import static org.keycloak.test.TestsHelper.deleteRealm;
import static org.keycloak.test.TestsHelper.importTestRealm;

/**
 * @author <a href="mailto:bruno@abstractj.org">Bruno Oliveira</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ArquillianJeeAuthzVanillaTest {

    private static final String WEBAPP_SRC = "src/main/webapp";
    private static final String APP_NAME = "app-authz-vanilla";

    @Page
    private LoginPage loginPage;

    @Page
    private AuthzPage indexPage;

    static {
        try {
            importTestRealm("admin", "admin", "/quickstart-realm.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deployment(name = APP_NAME, order = 1, testable = false)
    public static Archive createTestArchive() throws IOException {
        File[] files = Maven.resolver().loadPomFromFile("pom.xml")
                .importRuntimeDependencies().resolve().withTransitivity().asFile();

        return ShrinkWrap.create(WebArchive.class, "app-authz-jee-vanilla.war")
                .addPackages(true, Filters.exclude(".*Test.*"), Controller.class.getPackage())
                .addAsLibraries(files)
                .addAsWebResource(new File(WEBAPP_SRC, "index.jsp"))
                .addAsWebResource(new File(WEBAPP_SRC, "include-logout.jsp"))
                .addAsWebResource(new File(WEBAPP_SRC, "error.jsp"))
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource("keycloak.json")
                .setWebXML(new File("src/main/webapp", "WEB-INF/web.xml"));
    }

    @Drone
    private WebDriver webDriver;

    @ArquillianResource
    @OperateOnDeployment(APP_NAME)
    private URL contextRoot;

    @AfterClass
    public static void cleanUp() throws IOException {
        deleteRealm("admin", "admin", "quickstart-authz-vanilla");
    }

    @Before
    public void setup() {
        webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        webDriver.navigate().to(contextRoot);
    }

    @Test
    public void testAdminLogin() {
        try {
            loginPage.login("test-admin", "password");
            assertEquals("Should display restricted page with permissions",
                    "Your permissions are:",
                    indexPage.getMessage());
            indexPage.clickLogout();
        } catch (Exception e) {
            debugTest(e);
            fail("Should display the main page");
        }
    }

    @Test
    public void testAliceLogin() {
        try {
            loginPage.login("alice", "password");
            assertEquals("Should display restricted page with permissions",
                    "Your permissions are:",
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
