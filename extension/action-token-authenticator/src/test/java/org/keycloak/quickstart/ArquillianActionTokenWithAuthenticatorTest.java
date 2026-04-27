
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
import org.keycloak.quickstart.actiontoken.authenticator.ExternalAppAuthenticator;
import org.keycloak.quickstart.actiontoken.authenticator.ExternalAppAuthenticatorFactory;
import org.keycloak.representations.JsonWebToken;
import org.keycloak.representations.idm.*;
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
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static org.openqa.selenium.support.ui.ExpectedConditions.not;
import static org.openqa.selenium.support.ui.ExpectedConditions.urlToBe;

@ExtendWith(ArquillianExtension.class)
@KeycloakIntegrationTest(config = ArquillianActionTokenWithAuthenticatorTest.ServerConfig.class)
public class ArquillianActionTokenWithAuthenticatorTest {

    private static final String EXTERNAL_APP = "action-token-responder-example";

    private static final String KEYCLOAK_URL_BASE = "http://localhost:8080";
    private static final String KEYCLOAK_URL = KEYCLOAK_URL_BASE + "%s";
    private static final String REALM_QUICKSTART_ACTION_TOKEN = "quickstart-action-token";

    private static final String WEBAPP_SRC = "src/main/webapp";

    @InjectRealm(config = ArquillianActionTokenWithAuthenticatorTest.ActionTokenRealmConfig.class)
    static ManagedRealm realm;

    @Drone
    private WebDriver webDriver;

    @InjectOAuthClient
    OAuthClient oAuthClient;

    private static boolean authFlowConfigured = false;
    private static boolean userCreated = false;

    @Deployment(testable=false, name=EXTERNAL_APP)
    @TargetsContainer("wildfly")
    public static Archive<?> createTestArchive() {
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
        setupAuthenticationFlowIfNeeded();

        oAuthClient.openLoginForm();

        // Attempt to login as alice using WebDriver directly
        webDriver.findElement(org.openqa.selenium.By.id("username")).sendKeys("alice");
        webDriver.findElement(org.openqa.selenium.By.id("password")).sendKeys("password");
        webDriver.findElement(org.openqa.selenium.By.id("kc-login")).click();
        waitForPageToLoad();

        // Expect that the authenticator has redirected user to the external application
        String currentUrl = webDriver.getCurrentUrl();
        assertTrue(currentUrl.contains("/action-token-responder-example/external-action.jsp"),
                "Expected redirect to external app but was at: " + currentUrl);

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
        assertTrue(alice.getAttributes().containsKey(ExternalAppAuthenticator.DEFAULT_APPLICATION_ID + "." + "field_1"));
        assertEquals(List.of("abc def"), alice.getAttributes().get(ExternalAppAuthenticator.DEFAULT_APPLICATION_ID + "." + "field_1"));
        assertTrue(alice.getAttributes().containsKey(ExternalAppAuthenticator.DEFAULT_APPLICATION_ID + "." + "field_2"));
        assertEquals(List.of("ghi jkl"), alice.getAttributes().get(ExternalAppAuthenticator.DEFAULT_APPLICATION_ID + "." + "field_2"));
    }

    private void navigateTo(String path) {
        webDriver.navigate().to(format(KEYCLOAK_URL, path));
    }


    public static class ServerConfig implements KeycloakServerConfig {

        @Override
        public KeycloakServerConfigBuilder configure(KeycloakServerConfigBuilder config) {
            return config
                    .dependencyCurrentProject()
                    .spiOption("action-token-handler", "external-app-notification", "hmac-secret", "aSqzP4reFgWR4j94BDT1r+81QYp/NYbY9SBwXtqV1ko=");
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

    private void setupAuthenticationFlowIfNeeded() {
        if (authFlowConfigured) {
            return;
        }

        // Update authentication flow to use external application redirection
        realm.admin().flows().copy("browser", Map.of("newName", "browser-copy")).close();

        // Add the authenticator to the TOP-LEVEL browser-copy flow, not the forms subflow
        // This makes it execute AFTER authentication succeeds
        realm.admin().flows().addExecution("browser-copy",
                Map.of("provider", ExternalAppAuthenticatorFactory.ID)
        );

        // Get all executions from the TOP-LEVEL flow to find our newly added authenticator
        List<AuthenticationExecutionInfoRepresentation> browserExecutions = realm.admin().flows().getExecutions("browser-copy");
        AuthenticationExecutionInfoRepresentation extAppExecution = browserExecutions.stream()
                .filter(ex -> Objects.equals(ex.getProviderId(), ExternalAppAuthenticatorFactory.ID))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Could not find execution"));

        // Set it to REQUIRED and update on the TOP-LEVEL flow
        extAppExecution.setRequirement("REQUIRED");
        realm.admin().flows().updateExecutions("browser-copy", extAppExecution);

        // Configure the authenticator to use port 9080 for the external app (WildFly with port offset)
        AuthenticatorConfigRepresentation config = new AuthenticatorConfigRepresentation();
        config.setAlias("external-app-config");
        config.setConfig(Map.of(
                ExternalAppAuthenticatorFactory.CONFIG_APPLICATION_ID, ExternalAppAuthenticator.DEFAULT_APPLICATION_ID,
                ExternalAppAuthenticatorFactory.CONFIG_EXTERNAL_APP_URL, "http://localhost:9080/action-token-responder-example/external-action.jsp?token={TOKEN}"
        ));

        jakarta.ws.rs.core.Response configResponse = realm.admin().flows().newExecutionConfig(extAppExecution.getId(), config);
        assertEquals(201, configResponse.getStatus(), "Failed to create authenticator config");
        configResponse.close();

        // Set the new browser flow
        RealmRepresentation realmRep = realm.admin().toRepresentation();
        realmRep.setBrowserFlow("browser-copy");
        realm.admin().update(realmRep);

        // Enable unmanaged attributes of user profile
        UserProfileResource userProfileRes = realm.admin().users().userProfile();
        UPConfig cfg = userProfileRes.getConfiguration();
        cfg.setUnmanagedAttributePolicy(UPConfig.UnmanagedAttributePolicy.ENABLED);
        userProfileRes.update(cfg);

        authFlowConfigured = true;
    }

}
