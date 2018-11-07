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

import org.keycloak.Token;
import org.keycloak.TokenCategory;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.authentication.actiontoken.ActionTokenHandlerFactory;
import org.keycloak.common.util.Base64;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.quickstart.actiontoken.authenticator.ExternalAppAuthenticator;
import org.keycloak.quickstart.actiontoken.authenticator.ExternalAppAuthenticatorFactory;
import org.keycloak.quickstart.actiontoken.token.ExternalApplicationNotificationActionToken;
import org.keycloak.quickstart.actiontoken.token.ExternalApplicationNotificationActionTokenHandler;
import org.keycloak.quickstart.page.ExternalActionPage;
import org.keycloak.representations.JsonWebToken;
import org.keycloak.representations.idm.AuthenticationExecutionInfoRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.test.page.LoginPage;
import org.keycloak.util.JsonSerialization;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import static java.lang.String.format;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.keycloak.test.TestsHelper.deleteRealm;
import static org.keycloak.test.TestsHelper.importTestRealm;
import static org.keycloak.test.TestsHelper.keycloakBaseUrl;

@RunWith(Arquillian.class)
public class ArquillianActionTokenWithAuthenticatorTest {

    private static final String PROVIDER_JAR = "action-token-provider";
    private static final String EXTERNAL_APP = "action-token-responder-example";

    private static Keycloak ADMIN_CLIENT;
    private static final String KEYCLOAK_URL = "http://%s:%s/auth%s";
    private static final String REALM_QUICKSTART_ACTION_TOKEN = "quickstart-action-token";

    private static final String WEBAPP_SRC = "src/main/webapp";
    private static final String RESOURCES_SRC = "src/test/resources";

    @Page
    private LoginPage loginPage;

    @Page
    private ExternalActionPage externalActionPage;

    @Drone
    private WebDriver webDriver;

    @ArquillianResource
    @OperateOnDeployment(EXTERNAL_APP)
    private URL externalAppContextRoot;

    @ArquillianResource
    @OperateOnDeployment(PROVIDER_JAR)
    private URL keycloakContextRoot;

    @Deployment(testable=false, name=PROVIDER_JAR)
    @TargetsContainer("keycloak-remote")
    public static Archive<?> createProviderArchive() throws IOException {
        return ShrinkWrap.create(JavaArchive.class, "action-token-provider.jar")
                .addClasses(
                  ExternalApplicationNotificationActionToken.class,
                  ExternalApplicationNotificationActionTokenHandler.class,
                  ExternalAppAuthenticator.class,
                  ExternalAppAuthenticatorFactory.class)
                .addAsManifestResource(new File(RESOURCES_SRC, "MANIFEST.MF"))
                .addAsServiceProvider(AuthenticatorFactory.class, ExternalAppAuthenticatorFactory.class)
                .addAsServiceProvider(ActionTokenHandlerFactory.class, ExternalApplicationNotificationActionTokenHandler.class);
    }

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

    @BeforeClass
    public static void setupClass() throws Exception {
        ADMIN_CLIENT = Keycloak.getInstance(keycloakBaseUrl, "master", "admin", "admin", "admin-cli");
        final RealmResource qsRealm = ADMIN_CLIENT.realm(REALM_QUICKSTART_ACTION_TOKEN);

        // Import realm
        importTestRealm("admin", "admin", "/quickstart-realm.json");

        // Update authentication flow to use external application redirection
        qsRealm.flows().copy("browser", ImmutableMap.<String, String>builder().put("newName", "browser-copy").build()).close();
        qsRealm.flows().addExecution("browser-copy",
          ImmutableMap.<String, String>builder()
            .put("provider", ExternalAppAuthenticatorFactory.ID)
            .build());

        final List<AuthenticationExecutionInfoRepresentation> executions = qsRealm.flows().getExecutions("browser-copy");
        AuthenticationExecutionInfoRepresentation extAppExecution = executions.stream()
          .filter(ex -> Objects.equals(ex.getProviderId(), ExternalAppAuthenticatorFactory.ID))
          .findFirst()
          .orElseThrow(() -> new AssertionError("Could not find execution"));
        extAppExecution.setRequirement("REQUIRED");
        qsRealm.flows().updateExecutions("browser-copy", extAppExecution);

        final RealmRepresentation qsRealmRepresentation = qsRealm.toRepresentation();
        qsRealmRepresentation.setBrowserFlow("browser-copy");
        qsRealm.update(qsRealmRepresentation);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        deleteRealm("admin", "admin", REALM_QUICKSTART_ACTION_TOKEN);
    }

    @Before
    public void setup() {
        webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        navigateTo("/realms/" + REALM_QUICKSTART_ACTION_TOKEN + "/account/");
    }

    @Test
    public void testUserLogin() throws MalformedURLException, InterruptedException {
        // Attempt to login as alice
        loginPage.login("alice", "password");

        // Expect that the new required action has redirected user to the external application and fill in the form
        assertThat(webDriver.getCurrentUrl(), startsWith(externalAppContextRoot.toString()));
        externalActionPage.field1("abc def");
        externalActionPage.field2("ghi jkl");
        externalActionPage.submit();

        // Expect that the login has succeeded
        assertThat(webDriver.getCurrentUrl(), containsString("/account"));

        // Now check that the response from external application has been correctly handled by the custom action token handler
        final RealmResource qsRealm = ADMIN_CLIENT.realm(REALM_QUICKSTART_ACTION_TOKEN);
        List<UserRepresentation> aliceUsers = qsRealm.users().search("alice");
        assertThat(aliceUsers, hasSize(1));
        UserRepresentation alice = aliceUsers.get(0);
        assertThat(alice, notNullValue());
        assertThat(alice.getAttributes(), notNullValue());
        assertThat(alice.getAttributes(), hasEntry(is(ExternalAppAuthenticator.DEFAULT_APPLICATION_ID + "." + "field_1"), contains("abc def")));
        assertThat(alice.getAttributes(), hasEntry(is(ExternalAppAuthenticator.DEFAULT_APPLICATION_ID + "." + "field_2"), contains("ghi jkl")));
    }

    private void navigateTo(String path) {
        webDriver.navigate().to(format(KEYCLOAK_URL, keycloakContextRoot.getHost(), keycloakContextRoot.getPort(), path));
    }
}
