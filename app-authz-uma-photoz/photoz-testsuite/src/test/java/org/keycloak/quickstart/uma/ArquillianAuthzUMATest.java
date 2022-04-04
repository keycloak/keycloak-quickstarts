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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Config;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.quickstart.uma.page.LogoutConfirmPage;
import org.keycloak.quickstart.uma.page.PhotozPage;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.test.FluentTestsHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

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
    private static final String JS_POLICIES = "photoz-js-policies";
    private static final String DIRECT_GRANT_APP_NAME = "direct-grant";
    private static final String SECRET = "secret";
    private static final String VIEW_SCOPE = "album:view";
    private static final String DELETE_SCOPE = "album:delete";

    public static final String TEST_REALM = "photoz";
    public static final String KEYCLOAK_URL = "http://localhost:8180/auth";
    public static final FluentTestsHelper testHelper = new FluentTestsHelper(KEYCLOAK_URL,
            FluentTestsHelper.DEFAULT_ADMIN_USERNAME,
            FluentTestsHelper.DEFAULT_ADMIN_PASSWORD,
            FluentTestsHelper.DEFAULT_ADMIN_REALM,
            FluentTestsHelper.DEFAULT_ADMIN_CLIENT,
            FluentTestsHelper.DEFAULT_TEST_REALM);

    @Page
    private PhotozPage photozPage;

    @Page
    private LogoutConfirmPage logoutConfirmPage;

    @Drone
    private WebDriver webDriver;

    @ArquillianResource
    @OperateOnDeployment(HTML_CLIENT_APP_NAME)
    private URL contextRoot;

    static {
        try {
            testHelper.init();
        } catch (Exception e) {
            throw new IllegalStateException("Could not initialize Keycloak", e);
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

    @TargetsContainer("keycloak")
    @Deployment(name = JS_POLICIES, order = 3, testable = false)
    public static Archive createJsPoliciesArchive() throws IOException {
        return ShrinkWrap.create(ZipImporter.class, "photoz-js-policies.jar").importFrom(new File(
                "../photoz-js-policies/target/photoz-uma-js-policies.jar")).as(WebArchive.class);
    }

    @After
    public void cleanUp() {
        testHelper.deleteRealm(TEST_REALM);
    }

    @Before
    public void setup() {
        try {
            testHelper.importTestRealm("/quickstart-realm.json");

            ClientRepresentation directGrantClient = new ClientRepresentation();
            directGrantClient.setClientId(DIRECT_GRANT_APP_NAME);
            directGrantClient.setPublicClient(false);
            directGrantClient.setSecret(SECRET);
            directGrantClient.setDirectAccessGrantsEnabled(true);
            testHelper.getKeycloakInstance().realms().realm(TEST_REALM).clients().create(directGrantClient);
        } catch (Exception e) {
            throw new IllegalStateException("Could not initialize Keycloak", e);
        }
        webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        webDriver.navigate().to(contextRoot);
    }

    @Test
    public void testCreateDeleteAlbum() {
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

        logout();
    }

    @Test
    public void testRequestEntitlements() {
        photozPage.login("admin", "admin", "Admin Istrator");
        photozPage.requestEntitlements();
        String pageSource = webDriver.getPageSource();
        Assert.assertTrue(pageSource.contains("album:view"));
        Assert.assertTrue(pageSource.contains("album:delete"));
        Assert.assertTrue(pageSource.contains("admin:manage"));
        logout();

        photozPage.login("alice", "alice", null);
        photozPage.requestEntitlements();
        pageSource = webDriver.getPageSource();
        Assert.assertTrue(pageSource.contains("profile:view"));
        Assert.assertFalse(pageSource.contains("album:view"));
        Assert.assertFalse(pageSource.contains("admin:manage"));
        logout();
    }

    @Test
    public void testShareResource() {
        // login as alice, create an album, and share it with jdoe.
        photozPage.login("alice", "alice", null);
        photozPage.createAlbum("Germany Vacations");
        shareResource("alice", "alice", "jdoe", "Germany Vacations", VIEW_SCOPE, DELETE_SCOPE);
        photozPage.viewAlbum("Germany Vacations");
        webDriver.navigate().to(contextRoot);
        logout();

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
        logout();

        // log back in as alice and this time share the created album without granting delete permissions.
        photozPage.login("alice", "alice", null);
        photozPage.createAlbum("Greece Vacations");
        shareResource("alice", "alice", "jdoe", "Greece Vacations", VIEW_SCOPE);
        logout();

        // log back in as jdoe and attempt to delete the shared album.
        photozPage.login("jdoe", "jdoe", null);
        // link to deletion should not be displayed - in its place we should have the link to request delete access.
        Assert.assertFalse(webDriver.findElement(By.id("delete-share-Greece Vacations")).isDisplayed());
        Assert.assertTrue(webDriver.findElement(By.id("request-delete-share-Greece Vacations")).isDisplayed());
        // at this point jdoe can only request delete access for the shared album.
        photozPage.requestDeleteScope("Greece Vacations");

        // alice can now grant or deny jdoe's request. Let's grant it.
        grantRequestedPermission("alice", "alice", "Greece Vacations");
        webDriver.navigate().refresh();

        // jdoe should now be able to remove the shared album.
        photozPage.deleteSharedAlbum("Greece Vacations");
        Assert.assertTrue(emptySharedList.isDisplayed());
        logout();
    }

    private void grantRequestedPermission(String ownerUsername, String ownerPassword, String resourceName) {
        Client client = ResteasyClientBuilder.newClient();
        try {
            TokenManager tokenManager = getTokenManager(ownerUsername, ownerPassword, client);
            String resourceId = getResourceId(resourceName, tokenManager, client);

            // Get permission requests
            String requestsJson = client
                    .target(UriBuilder.fromUri(KEYCLOAK_URL).path("realms").path(TEST_REALM).path("account/resources").path(resourceId).path("permissions/requests"))
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .header("Authorization", "Bearer " + tokenManager.getAccessTokenString())
                    .get(String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode requests;

            try {
                requests = objectMapper.readTree(requestsJson);

                // Extract details from permission requests
                Assert.assertTrue(requests.isArray());
                Assert.assertEquals(1, requests.size());
                String user = requests.get(0).get("username").asText();
                String scopesJson = objectMapper.writeValueAsString(requests.get(0).get("scopes"));

                shareResource(resourceId, user, scopesJson, tokenManager, client);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        finally {
            client.close();
        }
    }

    private void shareResource(String ownerUsername, String ownerPassword, String user, String resourceName, String... scopes) {
        Client client = ResteasyClientBuilder.newClient();
        try {
            TokenManager tokenManager = getTokenManager(ownerUsername, ownerPassword, client);
            String resourceId = getResourceId(resourceName, tokenManager, client);

            // Permissions in Account API don't have a proper public representation (don't want to depend on whole services module)

            // Prepare new permissions
            try {
                String scopesJson = new ObjectMapper().writeValueAsString(scopes);

                shareResource(resourceId, user, scopesJson, tokenManager, client);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        finally {
            client.close();
        }
    }

    private void shareResource(String resourceId, String user, String scopesJson, TokenManager tokenManager, Client client) {
        String permissions = String.format("[{\"username\":\"%s\",\"scopes\":%s}]", user, scopesJson);

        // PUT new permissions
        Response response = client.target(UriBuilder.fromUri(KEYCLOAK_URL).path("realms").path(TEST_REALM).path("account/resources").path(resourceId).path("permissions"))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", "Bearer " + tokenManager.getAccessTokenString())
                .put(Entity.json(permissions));

        Assert.assertEquals(204, response.getStatus());

        client.close();
    }

    private String getResourceId(String resourceName, TokenManager tokenManager, Client client) {
        String jsonResponse = client
                .target(UriBuilder.fromUri(KEYCLOAK_URL).path("realms").path(TEST_REALM).path("account/resources"))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", "Bearer " + tokenManager.getAccessTokenString())
                .get(String.class);

        try {
            return StreamSupport.stream(new ObjectMapper().readTree(jsonResponse).spliterator(), false)
                    .filter(j -> resourceName.equals(j.get("name").asText()))
                    .map(j -> j.get("_id").asText())
                    .findFirst().get();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TokenManager getTokenManager(String username, String password, Client client) {
        return new TokenManager(
                new Config(KEYCLOAK_URL, TEST_REALM, username, password, DIRECT_GRANT_APP_NAME, SECRET),
                client
        );
    }

    private void logout() {
        photozPage.logout();
        logoutConfirmPage.confirmLogout();
    }
}