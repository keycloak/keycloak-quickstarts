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
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.drone.api.annotation.Drone;
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
import org.keycloak.Token;
import org.keycloak.TokenCategory;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserProfileResource;
import org.keycloak.common.util.Base64;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.quickstart.actiontoken.reqaction.RedirectToExternalApplication;
import org.keycloak.quickstart.test.FluentTestsHelper;
import org.keycloak.quickstart.page.ExternalActionPage;
import org.keycloak.representations.JsonWebToken;
import org.keycloak.representations.idm.RequiredActionProviderSimpleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.quickstart.test.page.LoginPage;
import org.keycloak.representations.userprofile.config.UPConfig;
import org.keycloak.util.JsonSerialization;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.FluentWait;
import static java.lang.String.format;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.keycloak.quickstart.test.TestsHelper.deleteRealm;
import static org.keycloak.quickstart.test.TestsHelper.importTestRealm;
import static org.keycloak.quickstart.test.TestsHelper.keycloakBaseUrl;
import static org.openqa.selenium.support.ui.ExpectedConditions.not;
import static org.openqa.selenium.support.ui.ExpectedConditions.urlToBe;

@RunWith(Arquillian.class)
public class ArquillianActionTokenTest {

    private static final String PROVIDER_JAR = "action-token-provider";
    private static final String EXTERNAL_APP = "action-token-responder-example";

    private static FluentTestsHelper fluentTestsHelper;
    private static final String KEYCLOAK_URL = keycloakBaseUrl + "%s";
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
        // Import realm
        importTestRealm("admin", "admin", "/quickstart-realm.json");

        fluentTestsHelper = new FluentTestsHelper(keycloakBaseUrl, "admin", "admin", "master", "admin-cli", REALM_QUICKSTART_ACTION_TOKEN).init();
        final RealmResource qsRealm = fluentTestsHelper.getKeycloakInstance().realm(REALM_QUICKSTART_ACTION_TOKEN);

        // Register the custom required action provider
        final RequiredActionProviderSimpleRepresentation requiredActionProvider = new RequiredActionProviderSimpleRepresentation();
        requiredActionProvider.setProviderId("redirect-to-external-application");
        requiredActionProvider.setName("Redirect to external application");
        qsRealm.flows().registerRequiredAction(requiredActionProvider);

        // Add the new required action to the list of "alice"'s actions required after her logon
        List<String> reqActions = Arrays.asList("redirect-to-external-application");
        qsRealm.users().search("alice").forEach(u -> {
            u.setRequiredActions(reqActions);
            qsRealm.users().get(u.getId()).update(u);
        });

        // Enable unmanaged attributes of user profile to be able to see the custom attributes
        UserProfileResource userProfileRes = qsRealm.users().userProfile();
        UPConfig cfg = userProfileRes.getConfiguration();
        cfg.setUnmanagedAttributePolicy(UPConfig.UnmanagedAttributePolicy.ENABLED);
        userProfileRes.update(cfg);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        deleteRealm("admin", "admin", REALM_QUICKSTART_ACTION_TOKEN);
    }

    @Before
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
        // Attempt to login as alice
        loginPage.login("alice", fluentTestsHelper.changePassword("alice", REALM_QUICKSTART_ACTION_TOKEN));
        waitForPageToLoad();

        // Expect that the new required action has redirected user to the external application and fill in the form
        assertThat(webDriver.getCurrentUrl(), startsWith(externalAppContextRoot.toString()));
        externalActionPage.field1("abc def");
        externalActionPage.field2("ghi jkl");
        externalActionPage.submit();
        waitForPageToLoad();

        // Expect that the login has succeeded
        assertThat(webDriver.getCurrentUrl(), containsString("/account"));

        // Now check that the response from external application has been correctly handled by the custom action token handler
        final RealmResource qsRealm = fluentTestsHelper.getKeycloakInstance().realm(REALM_QUICKSTART_ACTION_TOKEN);
        List<UserRepresentation> aliceUsers = qsRealm.users().search("alice");
        assertThat(aliceUsers, hasSize(1));
        UserRepresentation alice = aliceUsers.get(0);
        assertThat(alice, notNullValue());
        assertThat(alice.getAttributes(), notNullValue());
        assertThat(alice.getAttributes(), hasEntry(is(RedirectToExternalApplication.DEFAULT_APPLICATION_ID + "." + "field_1"), contains("abc def")));
        assertThat(alice.getAttributes(), hasEntry(is(RedirectToExternalApplication.DEFAULT_APPLICATION_ID + "." + "field_2"), contains("ghi jkl")));
    }

    private void navigateTo(String path) {
        webDriver.navigate().to(format(KEYCLOAK_URL, path));
    }
}
