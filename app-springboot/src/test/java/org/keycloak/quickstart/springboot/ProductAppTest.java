package org.keycloak.quickstart.springboot;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.quickstart.springboot.config.KeycloakSpringBootResolverConfig;
import org.keycloak.test.FluentTestsHelper;
import org.keycloak.test.TestsHelper;
import org.keycloak.test.builders.ClientBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.keycloak.test.builders.ClientBuilder.AccessType.BEARER_ONLY;
import static org.keycloak.test.builders.ClientBuilder.AccessType.PUBLIC;

/**
 * Created by sblanc on 3/28/17.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = {ProductApplication.class})
public class ProductAppTest {

    private WebClient webClient = new WebClient(BrowserVersion.CHROME);

    public static final String TEST_REALM = "quickstart";
    public static final String APP_URL = "http://localhost:8080";
    public static final FluentTestsHelper testHelper = new SpringTestHelper();

    @BeforeClass
    public static void setup() throws Exception {
        testHelper.init()
                .importTestRealm("/quickstart-realm.json")
                .createClient(ClientBuilder.create("test-demo").baseUrl(APP_URL).rootUrl(APP_URL).redirectUri(APP_URL + "/*").accessType(PUBLIC))
                .createClient(ClientBuilder.create("product-service").accessType(BEARER_ONLY))
                .createClient(ClientBuilder.create("test-dga").accessType(PUBLIC));
    }

    @AfterClass
    public static void cleanUp() {
        testHelper.deleteRealm(TEST_REALM);
    }

    @Test
    public void testRedirect() throws IOException {
        HtmlPage page = this.webClient.getPage("http://localhost:8080/products");
        Assert.assertTrue(page.getBody().getTextContent().contains("Username or email"));
    }

    @Test
    public void testLogin() throws Exception {
        HtmlPage page = this.webClient.getPage("http://localhost:8080/products");
        ((HtmlInput)page.getElementById("username")).setValueAttribute("alice");
        ((HtmlInput)page.getElementById("password")).setValueAttribute("password");
        HtmlPage protectedPage = page.getElementById("kc-login").click();
        Assert.assertTrue(protectedPage.getTitleText().contains("Product Page"));
    }

    @Test
    @Order(1)
    public void testLogout() throws IOException {
        try {
            HtmlPage page = this.webClient.getPage("http://localhost:8080/products");
            ((HtmlInput)page.getElementById("username")).setValueAttribute("alice");
            ((HtmlInput)page.getElementById("password")).setValueAttribute("password");
            HtmlPage protectedPage = page.getElementById("kc-login").click();
            HtmlPage landingPage = protectedPage.getElementById("logout").click();
            Assert.assertTrue(landingPage.getTitleText().contains("Landing Page"));
        } finally {
            webClient.getCookieManager().clearCookies();
        }

    }
}
