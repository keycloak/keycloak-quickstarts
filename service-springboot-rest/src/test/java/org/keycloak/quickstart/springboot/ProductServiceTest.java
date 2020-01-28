package org.keycloak.quickstart.springboot;


import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.test.FluentTestsHelper;
import org.keycloak.test.TestsHelper;
import org.keycloak.test.builders.ClientBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.keycloak.test.builders.ClientBuilder.AccessType.BEARER_ONLY;
import static org.keycloak.test.builders.ClientBuilder.AccessType.PUBLIC;

/**
 * Created by sblanc on 3/28/17.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {ProductServiceApplication.class})
public class ProductServiceTest {

    public static final String TEST_REALM = "quickstart";
    public static final String TEST_DGA = "test-dga";
    public static final String APP_URL = "http://localhost:8081";
    public static final FluentTestsHelper testHelper = new SpringTestHelper();

    @BeforeClass
    public static void setup() throws Exception {
        testHelper.init()
                .importTestRealm("/quickstart-realm.json")
                .createClient(ClientBuilder.create("test-demo").baseUrl(APP_URL).rootUrl(APP_URL).redirectUri(APP_URL + "/*").accessType(PUBLIC))
                .createClient(ClientBuilder.create("product-service").accessType(BEARER_ONLY))
                .createClient(ClientBuilder.create(TEST_DGA).accessType(PUBLIC));
    }

    @AfterClass
    public static void cleanUp() {
        testHelper.deleteRealm(TEST_REALM);
    }

    @Test
    public void testUnauthenticatedSecuredEndpoint()  {
        Assert.assertEquals(401, get(APP_URL + "/products").getStatus());
    }

    @Test
    public void testAuthenticatedSecuredEndpoint()  {
        String tokenForAlice = new SpringTestHelper("alice", "password", TEST_REALM, TEST_DGA).initWithoutInitialToken().getToken();
        Assert.assertEquals(200, get(APP_URL + "/products", tokenForAlice).getStatus());
    }

    public Response get(String uri) {
        return get(uri, null);
    }

    public Response get(String uri, String token) {
        Client client = javax.ws.rs.client.ClientBuilder.newClient();
        Response response = null;
        try {
            WebTarget target = client.target(uri);
            Invocation.Builder request = target.request();
            if (token != null)
                request.header("Authorization", "Bearer " + token);
            response = request.get();
            response.close();
        } finally {
            client.close();
        }
        return response;
    }

}
