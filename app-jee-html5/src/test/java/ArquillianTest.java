import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.quickstart.page.IndexPage;
import org.keycloak.quickstart.page.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.keycloak.quickstart.utils.WaitUtils.waitForPageToLoad;
import static org.keycloak.quickstart.utils.WaitUtils.waitTextToBePresent;

/**
 * Created by abstractj on 3/13/17.
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ArquillianTest {

    private static final String WEBAPP_SRC = "src/main/webapp";

    @Page
    private IndexPage indexPage;

    @Page
    private LoginPage loginPage;

    @Deployment(name= "service-jee-jaxrs", order = 1, testable = false)
    public static Archive<?> createTestArchive1() throws IOException {
        return ShrinkWrap.createFromZipFile(WebArchive.class,
                new File("../service-jee-jaxrs/target/service.war"));
    }

    @Deployment(name= "app-html5", order = 2, testable = false)
    public static Archive<?> createTestArchive2() throws IOException {
        return ShrinkWrap.create(WebArchive.class, "app-html5.war")
                .addAsWebResource(new File(WEBAPP_SRC, "app.js"))
                .addAsWebResource(new File(WEBAPP_SRC, "index.html"))
                .addAsWebResource(new File(WEBAPP_SRC, "keycloak.js"))
                .addAsWebResource(new File(WEBAPP_SRC, "styles.css"))
                .addAsWebResource(new File("config", "keycloak.json"));

    }

    @Drone
    private WebDriver webDriver;

    @ArquillianResource
    @OperateOnDeployment("app-html5")
    private URL contextRoot;

    @Test
    public void testSecuredResource() throws InterruptedException {
        try {
            webDriver.navigate().to(contextRoot);
            indexPage.clickSecured();
            assertTrue(indexPage.isUnauthorized(webDriver));
        } catch (Exception e) {
            fail("Should display an error message");
        }
    }

    @Test
    public void testAdminResource() {
        try {
            webDriver.navigate().to(contextRoot);
            indexPage.clickAdmin();
            assertTrue(indexPage.isUnauthorized(webDriver));
        } catch (Exception e) {
            fail("Should display an error message");
        }
    }

    @Test
    public void testPublicResource() {
        try {
            webDriver.navigate().to(contextRoot);
            indexPage.clickPublic();
            assertTrue(indexPage.isPublic(webDriver));
        } catch (Exception e) {
            fail("Should display an error message");
        }
    }

    @Test
    public void testAdminWithAuthAndRole() throws MalformedURLException, InterruptedException {
        try {
            webDriver.navigate().to(contextRoot);
            indexPage.clickLogin();
            waitForPageToLoad(webDriver);
            loginPage.login("admin", "admin");
            waitForPageToLoad(webDriver);
            indexPage.clickAdmin();
            waitTextToBePresent(webDriver, By.id("message"), "User: admin");
            assertTrue(indexPage.getMessage().contains("admin"));
            indexPage.clickLogout();
        } catch (Exception e) {
            fail("Should display logged in user");
        }
    }
}
