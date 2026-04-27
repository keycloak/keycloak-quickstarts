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
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.Token;
import org.keycloak.TokenCategory;
import org.keycloak.admin.client.resource.UserProfileResource;
import org.keycloak.common.util.Base64;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.quickstart.actiontoken.reqaction.RedirectToExternalApplication;
import org.keycloak.representations.JsonWebToken;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RequiredActionProviderSimpleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.userprofile.config.UPConfig;
import org.keycloak.testframework.annotations.InjectRealm;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;
import org.keycloak.testframework.oauth.OAuthClient;
import org.keycloak.testframework.oauth.annotations.InjectOAuthClient;
import org.keycloak.testframework.realm.ManagedRealm;
import org.keycloak.testframework.realm.RealmConfig;
import org.keycloak.testframework.realm.RealmConfigBuilder;
import org.keycloak.testframework.server.KeycloakServerConfig;
import org.keycloak.testframework.server.KeycloakServerConfigBuilder;
import org.keycloak.util.JsonSerialization;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static org.openqa.selenium.support.ui.ExpectedConditions.not;
import static org.openqa.selenium.support.ui.ExpectedConditions.urlToBe;

@ExtendWith(ArquillianExtension.class)
@KeycloakIntegrationTest(config = ArquillianActionTokenTest.ServerConfig.class)
public class ArquillianActionTokenTest {

    private static final String EXTERNAL_APP = "action-token-responder-example";

    private static final String KEYCLOAK_URL_BASE = "http://localhost:8080";
    private static final String KEYCLOAK_URL = KEYCLOAK_URL_BASE + "%s";
    private static final String REALM_QUICKSTART_ACTION_TOKEN = "quickstart-action-token";

    private static final String WEBAPP_SRC = "src/main/webapp";

    @InjectRealm(config = ArquillianActionTokenTest.ActionTokenRealmConfig.class)
    static ManagedRealm realm;

    @Drone
    private WebDriver webDriver;

    @InjectOAuthClient
    OAuthClient oAuthClient;

    private static boolean requiredActionConfigured = false;
    private static boolean userCreated = false;

    @Deployment(testable=false, name=EXTERNAL_APP)
    @TargetsContainer("wildfly")
    public static Archive<?> createTestArchive() throws IOException {
        return ShrinkWrap.create(WebArchive.class, "action-token-responder-example.war")
          .setWebXML(new File(WEBAPP_SRC, "WEB-INF/web.xml"))
          .addAsWebInfResource(new File(WEBAPP_SRC, "WEB-INF/jboss-deployment-structure.xml"))
          .addAsWebResource(new File(WEBAPP_SRC, "external-action.jsp"))
          .addAsWebResource(new File(WEBAPP_SRC, "submit-back.jsp"))

          // Few Keycloak dependencies handling JWT serialization
          .addClasses(JsonWebToken.class, Token.class, TokenCategory.class)
          .addPackages(true, Base64.class.getPackage(), JWSInput.class.getPackage(), JsonSerialization.class.getPackage())
          .addPackages(true, "org.keycloak.json")
          ;
    }

    @BeforeEach
    public void setup() {
        webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        navigateTo("/realms/" + REALM_QUICKSTART_ACTION_TOKEN + "/account/#/");
        waitForPageToLoad();
    }

    public void waitForPageToLoad() {
        // Taken from org.keycloak.testsuite.util.WaitUtils

        String currentUrl = null;

        // Ensure the URL is "stable", i.e. is not changing anymore; if it'd changing, some redirects are probably still in progress
        for (int maxRedirects = 4; maxRedirects > 0; maxRedirects--) {
            currentUrl = webDriver.getCurrentUrl();
            FluentWait<WebDriver> wait = new FluentWait<>(webDriver).withTimeout(Duration.ofMillis(250));
            try {
                wait.until(not(urlToBe(currentUrl)));
            }
            catch (TimeoutException e) {
                break; // URL has not changed recently - ok, the URL is stable and page is current
            }
        }
    }

    @Test
    public void testUserLogin() {

        createUserIfNeeded();
        setupRequiredActionIfNeeded();

        oAuthClient.openLoginForm();

        // Attempt to login as alice using WebDriver directly
        webDriver.findElement(org.openqa.selenium.By.id("username")).sendKeys("alice");
        webDriver.findElement(org.openqa.selenium.By.id("password")).sendKeys("password");
        webDriver.findElement(org.openqa.selenium.By.id("kc-login")).click();
        waitForPageToLoad();

        // Expect that the new required action has redirected user to the external application and fill in the form
        assertTrue(Objects.requireNonNull(webDriver.getCurrentUrl()).contains("/action-token-responder-example/external-action.jsp"));

        // Set field values and submit using JavaScript to ensure proper form submission
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) webDriver;
        js.executeScript(
            "document.getElementsByName('field_1')[0].value = 'abc def';" +
            "document.getElementsByName('field_2')[0].value = 'ghi jkl';" +
            "document.querySelector('form').submit();"
        );
        waitForPageToLoad();
        // Wait for the submit-back.jsp redirect to complete
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        waitForPageToLoad();

        // Expect that the login has succeeded
        String finalUrl = webDriver.getCurrentUrl();
        assertTrue(finalUrl.contains("/account"),
            "Expected URL to contain '/account' but was: " + finalUrl);

        // Now check that the response from external application has been correctly handled by the custom action token handler
        List<UserRepresentation> aliceUsers = realm.admin().users().search("alice");
        assertEquals(1, aliceUsers.size());
        UserRepresentation alice = aliceUsers.get(0);
        assertNotNull(alice);
        assertNotNull(alice.getAttributes());
        assertTrue(alice.getAttributes().containsKey(RedirectToExternalApplication.DEFAULT_APPLICATION_ID + "." + "field_1"));
        assertEquals(List.of("abc def"), alice.getAttributes().get(RedirectToExternalApplication.DEFAULT_APPLICATION_ID + "." + "field_1"));
        assertTrue(alice.getAttributes().containsKey(RedirectToExternalApplication.DEFAULT_APPLICATION_ID + "." + "field_2"));
        assertEquals(List.of("ghi jkl"), alice.getAttributes().get(RedirectToExternalApplication.DEFAULT_APPLICATION_ID + "." + "field_2"));
    }

    private void navigateTo(String path) {
        webDriver.navigate().to(format(KEYCLOAK_URL, path));
    }

    private void createUserIfNeeded() {
        if (userCreated) {
            return;
        }

        UserRepresentation alice = new UserRepresentation();
        alice.setUsername("alice");
        alice.setEmail("alice@keycloak.org");
        alice.setFirstName("Alice");
        alice.setLastName("Liddel");
        alice.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue("password");
        credential.setTemporary(false);
        alice.setCredentials(List.of(credential));

        jakarta.ws.rs.core.Response response = realm.admin().users().create(alice);
        response.close();

        userCreated = true;
    }

    private void setupRequiredActionIfNeeded() {
        if (requiredActionConfigured) {
            return;
        }

        // Register the custom required action provider
        RequiredActionProviderSimpleRepresentation requiredActionProvider = new RequiredActionProviderSimpleRepresentation();
        requiredActionProvider.setProviderId("redirect-to-external-application");
        requiredActionProvider.setName("Redirect to external application");
        realm.admin().flows().registerRequiredAction(requiredActionProvider);

        // Add the new required action to alice user
        List<String> reqActions = Arrays.asList("redirect-to-external-application");
        realm.admin().users().search("alice").forEach(u -> {
            u.setRequiredActions(reqActions);
            realm.admin().users().get(u.getId()).update(u);
        });

        // Enable unmanaged attributes of user profile
        UserProfileResource userProfileRes = realm.admin().users().userProfile();
        UPConfig cfg = userProfileRes.getConfiguration();
        cfg.setUnmanagedAttributePolicy(UPConfig.UnmanagedAttributePolicy.ENABLED);
        userProfileRes.update(cfg);

        requiredActionConfigured = true;
    }

    public static class ServerConfig implements KeycloakServerConfig {

        @Override
        public KeycloakServerConfigBuilder configure(KeycloakServerConfigBuilder config) {
            return config
                    .dependencyCurrentProject()
                    .spiOption("action-token-handler", "external-app-reqaction-notification", "hmac-secret", "aSqzP4reFgWR4j94BDT1r+81QYp/NYbY9SBwXtqV1ko=")
                    .spiOption("required-action", "redirect-to-external-application", "external-application-url", "http://localhost:9080/action-token-responder-example/external-action.jsp?token={TOKEN}");
        }
    }

    static class ActionTokenRealmConfig implements RealmConfig {

        @Override
        public RealmConfigBuilder configure(RealmConfigBuilder realmConfigBuilder) {
            return realmConfigBuilder
                    .name("quickstart-action-token")
                    .sslRequired("external")
                    .registrationAllowed(true);
        }
    }
}
