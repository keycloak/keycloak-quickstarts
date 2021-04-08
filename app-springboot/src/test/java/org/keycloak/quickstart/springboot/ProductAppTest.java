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
import org.keycloak.test.FluentTestsHelper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * Created by sblanc on 3/28/17.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {ProductApplication.class})
public class ProductAppTest {

    public static final String KEYCLOAK_URL = "http://localhost:8180/auth";

    private WebClient webClient = new WebClient(BrowserVersion.CHROME);
    private static FluentTestsHelper testsHelper;

    @BeforeClass
    public static void setup() throws IOException {
        testsHelper = new FluentTestsHelper(KEYCLOAK_URL,
                "admin", "admin",
                FluentTestsHelper.DEFAULT_ADMIN_REALM,
                FluentTestsHelper.DEFAULT_ADMIN_CLIENT,
                FluentTestsHelper.DEFAULT_TEST_REALM)
                .init();

        testsHelper.importTestRealm("/quickstart-realm.json");
    }

    @AfterClass
    public static void cleanUp() {
        testsHelper.deleteTestRealm();

        if (testsHelper != null) {
            testsHelper.close();
        }
    }

    @Test
    public void testRedirect() throws IOException {
        HtmlPage page = this.webClient.getPage("http://localhost:8080/products");
        Assert.assertTrue(page.getBody().getTextContent().contains("Username or email"));
    }

    @Test
    public void testLogin() throws IOException {
        HtmlPage page = this.webClient.getPage("http://localhost:8080/products");
        ((HtmlInput)page.getElementById("username")).setValueAttribute("alice");
        ((HtmlInput)page.getElementById("password")).setValueAttribute("password");
        HtmlPage protectedPage = page.getElementByName("login").click();
        Assert.assertTrue(protectedPage.getTitleText().contains("Product Page"));
    }

    @Test
    public void testLogout() throws IOException {
        HtmlPage page = this.webClient.getPage("http://localhost:8080/products");
        ((HtmlInput)page.getElementById("username")).setValueAttribute("alice");
        ((HtmlInput)page.getElementById("password")).setValueAttribute("password");
        HtmlPage protectedPage = page.getElementByName("login").click();
        HtmlPage landingPage = protectedPage.getElementById("logout").click();
        Assert.assertTrue(landingPage.getTitleText().contains("Landing Page"));
    }
}
