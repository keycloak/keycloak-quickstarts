/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates
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

package org.keycloak.quickstart.uma;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.quickstart.uma.page.PhotozPage;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.authorization.ResourceServerRepresentation;
import org.keycloak.util.JsonSerialization;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.fail;
import static org.keycloak.test.TestsHelper.deleteRealm;
import static org.keycloak.test.TestsHelper.importTestRealm;

/**
 * Tests for the {@code Photoz} quickstart.
 *
 * @author <a href="mailto:sguilhen@redhat.com">Stefan Guilhen</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ArquillianAuthzUMATest {

    private static final String REALM_NAME = "photoz";
    private static final String HTML_CLIENT_APP_NAME = "photoz-html5-client";
    private static final String RESTFUL_API_APP_NAME = "photoz-restful-api";

    @Page
    private PhotozPage photozPage;

    @Drone
    private WebDriver webDriver;

    @ArquillianResource
    @OperateOnDeployment(HTML_CLIENT_APP_NAME)
    private URL contextRoot;

    static {
        try {
            importTestRealm("admin", "admin", "/quickstart-realm.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Deployment(name = HTML_CLIENT_APP_NAME, order = 1, testable = false)
    public static Archive createHtmlClientArchive() throws IOException {
        return ShrinkWrap.create(ZipImporter.class, "photoz-html5-client.war").importFrom(
                new File("../photoz-html5-client/target/photoz-uma-html5-client.war")).as(WebArchive.class);
    }

    @Deployment(name = RESTFUL_API_APP_NAME, order = 2, testable = false)
    public static Archive createRestfulAPIArchive() throws IOException {
        return ShrinkWrap.create(ZipImporter.class, "photoz-restful-api.war").importFrom(new File(
                "../photoz-restful-api/target/photoz-uma-restful-api.war")).as(WebArchive.class);
    }

    @AfterClass
    public static void cleanUp() throws IOException {
        deleteRealm("admin", "admin", "photoz");
    }

    @Before
    public void setup() {
        webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        webDriver.navigate().to(contextRoot);
    }

    @Test
    public void testCreateDeleteAlbum() {
        try {
            photozPage.login("alice", "alice", "Alice In Chains");

            // the albums and shared albums lists should be empty.
            WebElement emptyAlbumsList = webDriver.findElement(By.id("resource-list-empty"));
            Assert.assertTrue(emptyAlbumsList.isDisplayed());
            Assert.assertEquals("You don't have any albums, yet.", emptyAlbumsList.getText());
            WebElement emptySharedList = webDriver.findElement(By.id("share-list-empty"));
            Assert.assertTrue(emptySharedList.isDisplayed());
            Assert.assertEquals("You don't have any shares, yet.", emptySharedList.getText());

            // create an album and check the list of albums in no longer empty.
            photozPage.createAlbum("France Vacations");
            Assert.assertFalse(emptyAlbumsList.isDisplayed());

            // now delete the created album and verify the list of albums is empty again.
            photozPage.deleteAlbum("France Vacations");
            Assert.assertTrue(emptyAlbumsList.isDisplayed());

            photozPage.logout();
        } catch (Exception e) {
            debugTest(e);
            fail("Should display the main page");
        }
    }

    @Test
    public void testRequestEntitlements() {
        try {
            photozPage.login("admin", "admin", "Admin Istrator");
            photozPage.requestEntitlements();
            String pageSource = webDriver.getPageSource();
            Assert.assertTrue(pageSource.contains("album:view"));
            Assert.assertTrue(pageSource.contains("album:delete"));
            Assert.assertTrue(pageSource.contains("admin:manage"));
            photozPage.logout();

            photozPage.login("alice", "alice", null);
            photozPage.requestEntitlements();
            pageSource = webDriver.getPageSource();
            Assert.assertTrue(pageSource.contains("profile:view"));
            Assert.assertFalse(pageSource.contains("album:view"));
            Assert.assertFalse(pageSource.contains("admin:manage"));
            photozPage.logout();
        } catch (Exception e) {
            debugTest(e);
            fail("Should display the entitlements");
        }
    }

    @Test
    public void testShareResource() {
        try {
            // login as alice, create an album, and share it with jdoe.
            photozPage.login("alice", "alice", null);
            photozPage.createAlbum("Germany Vacations");
            photozPage.shareResource("Germany Vacations", "jdoe");
            photozPage.viewAlbum("Germany Vacations");
            webDriver.navigate().to(contextRoot);
            photozPage.logout();

            photozPage.login("jdoe", "jdoe", null);
            // jdoe's album list should be empty, but shared albums list shouldn't.
            WebElement emptyAlbumsList = webDriver.findElement(By.id("resource-list-empty"));
            Assert.assertTrue(emptyAlbumsList.isDisplayed());
            Assert.assertEquals("You don't have any albums, yet.", emptyAlbumsList.getText());
            WebElement emptySharedList = webDriver.findElement(By.id("share-list-empty"));
            Assert.assertFalse(emptySharedList.isDisplayed());
            Assert.assertTrue(webDriver.findElement(By.id("delete-share-Germany Vacations")).isDisplayed());

            // jdoe should be able to delete alice's shared album.
            photozPage.deleteSharedAlbum("Germany Vacations");
            Assert.assertTrue(emptySharedList.isDisplayed());
            photozPage.logout();

            // log back in as alice and this time share the created album without granting delete permissions.
            photozPage.login("alice", "alice", null);
            photozPage.createAlbum("Greece Vacations");
            photozPage.shareResourceWithExcludedScope("Greece Vacations", "jdoe", "album:delete");
            photozPage.logout();

            // log back in as jdoe and attempt to delete the shared album.
            photozPage.login("jdoe", "jdoe", null);
            // link to deletion should not be displayed - in its place we should have the link to request delete access.
            Assert.assertFalse(webDriver.findElement(By.id("delete-share-Greece Vacations")).isDisplayed());
            Assert.assertTrue(webDriver.findElement(By.id("request-delete-share-Greece Vacations")).isDisplayed());
            // at this point jdoe can only request delete access for the shared album.
            photozPage.requestDeleteScope("Greece Vacations");
            photozPage.logout();

            // alice can now grant or deny jdoe's request. Let's grant it.
            photozPage.login("alice", "alice", null);
            photozPage.grantRequestedPermission("Greece Vacations", "jdoe");
            photozPage.logout();

            // jdoe should now be able to remove the shared album.
            photozPage.login("jdoe", "jdoe", null);
            photozPage.deleteSharedAlbum("Greece Vacations");
            Assert.assertTrue(emptySharedList.isDisplayed());
            photozPage.logout();
        } catch (Exception e) {
            debugTest(e);
            fail("Should have been able to share resource");
        }
    }

    private void debugTest(Exception e) {
        System.out.println(webDriver.getPageSource());
        e.printStackTrace();
    }
}