package org.keycloak.quickstart.springboot;


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

/**
 * Created by sblanc on 3/28/17.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {ProductServiceApplication.class})

public class ProductServiceTest {

    @BeforeClass
    public static void setup() throws IOException {
        TestsHelper.baseUrl = "http://localhost:8081";
        //TestsHelper.keycloakBaseUrl  = "set keycloak server docker IP"
        TestsHelper.testRealm="quickstart";
        TestsHelper.importTestRealm("admin","admin","/quickstart-realm.json");
        TestsHelper.createDirectGrantClient();
        TestsHelper.createClient(ClientBuilder.create("test-demo")
                .baseUrl(TestsHelper.baseUrl).accessType(BEARER_ONLY));

    }

    @Test
    public void testUnauthenticatedSecuredEndpoint()  {

        try {
            Assert.assertTrue(TestsHelper.returnsForbidden("/products"));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testAuthenticatedSecuredEndpoint()  {
        try {
            Assert.assertTrue(TestsHelper.testGetWithAuth("/products", TestsHelper.getToken("alice","password",TestsHelper.testRealm)));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @AfterClass
    public static void cleanUp() throws IOException{
        TestsHelper.deleteRealm("admin","admin",TestsHelper.testRealm);
    }

}
