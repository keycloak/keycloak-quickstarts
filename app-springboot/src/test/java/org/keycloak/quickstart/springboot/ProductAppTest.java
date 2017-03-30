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
import org.keycloak.test.TestsHelper;
import org.keycloak.test.builders.ClientBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.keycloak.test.builders.ClientBuilder.AccessType.BEARER_ONLY;
import static org.keycloak.test.builders.ClientBuilder.AccessType.PUBLIC;

/**
 * Created by sblanc on 3/28/17.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {ProductApplication.class})
public class ProductAppTest {

    private WebClient webClient = new WebClient(BrowserVersion.CHROME);

    @BeforeClass
    public static void setup() throws IOException {
        TestsHelper.baseUrl = "http://localhost:8080";
        //TestsHelper.keycloakBaseUrl  = "set keycloak server docker IP"
        TestsHelper.testRealm="quickstart";
        TestsHelper.initialAccessTokenCount = 3;
        TestsHelper.importTestRealm("admin","admin","/quickstart-realm.json");
        TestsHelper.createDirectGrantClient();
        TestsHelper.createClient(ClientBuilder.create("test-demo").baseUrl(TestsHelper.baseUrl)
                .rootUrl("http://localhost:8080").accessType(PUBLIC));
        TestsHelper.createClient(ClientBuilder.create("product-service").accessType(BEARER_ONLY));

    }

    @AfterClass
    public static void cleanUp() throws IOException{
        TestsHelper.deleteRealm("admin","admin",TestsHelper.testRealm);
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
