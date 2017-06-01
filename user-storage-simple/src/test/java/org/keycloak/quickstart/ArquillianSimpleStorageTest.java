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
import org.junit.Ignore;
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
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.keycloak.quickstart.util.StorageManager.addUser;
import static org.keycloak.quickstart.util.StorageManager.createStorage;
import static org.keycloak.quickstart.util.StorageManager.destroy;
import static org.keycloak.quickstart.util.StorageManager.getPropertyFile;


@RunWith(Arquillian.class)
public class ArquillianSimpleStorageTest {

    private static final String KEYCLOAK_ADMIN = "http://%s:%s/auth/admin";

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
        navigateToRealmConsole();
    }

    private void navigateToRealmConsole() {
        webDriver.navigate().to(format(KEYCLOAK_ADMIN,
                contextRoot.getHost(), contextRoot.getPort()));
    }

    @Ignore
    @Test
    public void testUserReadOnlyFederationStorage() throws MalformedURLException, InterruptedException {
        try {
            loginPage.login("admin", "admin");
            consolePage.createReadOnlyStorage();
            consolePage.navigateToUserFederationMenu();
            assertNotNull("Storage provider should be created", consolePage.readOnlyStorageLink());
            consolePage.logout();

            loginPage.login("tbrady", "superbowl");
            assertEquals("Should display the user from storage provider", "Tbrady", consolePage.getUser());
            consolePage.logout();

            navigateToRealmConsole();
            loginPage.login("admin", "admin");
            consolePage.delete();
            consolePage.logout();
        } catch (Exception e) {
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
            consolePage.logout();

            loginPage.login("malcom", "butler");
            assertEquals("Should display the user from storage provider", "Malcom", consolePage.getUser());
            consolePage.logout();

            addUser("rob", "gronkowski");
            navigateToRealmConsole();
            loginPage.login("rob", "gronkowski");
            assertEquals("Should display the user from storage provider", "Rob", consolePage.getUser());
            consolePage.logout();

            navigateToRealmConsole();
            loginPage.login("admin", "admin");
            consolePage.delete();
            consolePage.logout();
            destroy();
        } catch (Exception e) {
            fail("Should create a user federation storage");
        }
    }
}
