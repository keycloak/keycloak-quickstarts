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

package org.keycloak.quickstart;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.quickstart.page.ConsolePage;
import org.keycloak.quickstart.util.StorageManager;
import org.keycloak.test.page.LoginPage;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static java.lang.String.format;
import java.util.concurrent.TimeUnit;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.keycloak.quickstart.util.StorageManager.addUser;
import static org.keycloak.quickstart.util.StorageManager.createStorage;
import static org.keycloak.quickstart.util.StorageManager.deleteStorage;
import static org.keycloak.quickstart.util.StorageManager.getPropertyFile;


@RunWith(Arquillian.class)
public class ArquillianSimpleStorageTest {

    private static final String KEYCLOAK_URL = "http://%s:%s/auth%s";

    @Page
    private LoginPage loginPage;

    @Page
    private ConsolePage consolePage;

    @Drone
    private WebDriver webDriver;

    @ArquillianResource
    private URL contextRoot;

    @Deployment(testable = false)
    public static Archive<?> createTestArchive() throws IOException {
        return ShrinkWrap.create(JavaArchive.class, "user-storage-simple-example.jar")
                .addClasses(
                        StorageManager.class,
                        org.keycloak.quickstart.readonly.PropertyFileUserStorageProvider.class,
                        org.keycloak.quickstart.readonly.PropertyFileUserStorageProviderFactory.class,
                        org.keycloak.quickstart.writeable.PropertyFileUserStorageProvider.class,
                        org.keycloak.quickstart.writeable.PropertyFileUserStorageProviderFactory.class)
                .addAsResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource("META-INF/services/org.keycloak.storage.UserStorageProviderFactory")
                .addAsResource("users.properties");

    }


    @Before
    public void setup() {
        navigateTo("/admin");
    }

    private void navigateTo(String path) {
        webDriver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
        webDriver.navigate().to(format(KEYCLOAK_URL,
                contextRoot.getHost(), contextRoot.getPort(), path));
    }

    @Test
    public void testUserReadOnlyFederationStorage() throws MalformedURLException, InterruptedException {
        try {
            loginPage.login("admin", "admin");
            consolePage.createReadOnlyStorage();
            consolePage.navigateToUserFederationMenu();
            assertNotNull("Storage provider should be created", consolePage.readOnlyStorageLink());

            navigateTo("/realms/master/account");
            assertEquals("Should display admin", "admin", consolePage.getUser());
            consolePage.logout();

            navigateToAccount("tbrady", "superbowl");
            assertEquals("Should display the user from storage provider", "tbrady", consolePage.getUser());
            consolePage.logout();

            removeProvider();
        } catch (Exception e) {
            debugTest(e);
            fail("Should create a user federation storage");
        }
    }

    @Test
    public void testUserWritableFederationStorage() throws MalformedURLException, InterruptedException {
        try {
            createStorage();
            addUser("malcom", "butler");

            loginPage.login("admin", "admin");
            consolePage.selectWritableStorage();
            consolePage.setFileStoragePath(getPropertyFile());
            consolePage.save();

            assertNotNull("Storage provider should be created", consolePage.writableStorageLink());
            navigateTo("/realms/master/account");
            assertEquals("Should display admin", "admin", consolePage.getUser());
            consolePage.logout();

            navigateToAccount("malcom", "butler");
            assertEquals("Should display the user from storage provider", "malcom", consolePage.getUser());
            consolePage.logout();

            addUser("rob", "gronkowski");
            navigateToAccount("rob", "gronkowski");
            assertEquals("Should display the user from storage provider", "rob", consolePage.getUser());
            consolePage.logout();

            removeProvider();
            deleteStorage();
        } catch (Exception e) {
            debugTest(e);
            fail("Should create a user federation storage");
        }
    }

    private void removeProvider() {
        navigateTo("/admin");
        loginPage.login("admin", "admin");
        consolePage.delete();
        navigateTo("/realms/master/account");
        consolePage.logout();
    }

    private void navigateToAccount(String user, String password) {
        loginPage.login(user, password);
        navigateTo("/realms/master/account");
    }
    
    private void debugTest(Exception e) {
        System.out.println(webDriver.getPageSource());
        e.printStackTrace();
    }
}
