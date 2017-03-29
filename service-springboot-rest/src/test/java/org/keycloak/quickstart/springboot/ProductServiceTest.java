package org.keycloak.quickstart.springboot;


import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.helper.TestsHelper;
import org.keycloak.representations.idm.ClientRepresentation;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

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
        TestsHelper.ImportTestRealm("admin","admin","/quickstart-realm.json");
        TestsHelper.createDirectGrantClient();
        TestsHelper.createClient(generateClientRepresentation());

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

    private static ClientRepresentation generateClientRepresentation() {
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setClientId("test-demo");
        clientRepresentation.setBaseUrl(TestsHelper.baseUrl);
        clientRepresentation.setBearerOnly(true);
        return clientRepresentation;
    }

    @AfterClass
    public static void cleanUp() throws IOException{
        TestsHelper.deleteRealm("admin","admin",TestsHelper.testRealm);
    }

}
