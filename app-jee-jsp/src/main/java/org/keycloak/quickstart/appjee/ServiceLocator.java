package org.keycloak.quickstart.appjee;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author <a href="mailto:mstrukel@redhat.com">Marko Strukelj</a>
 */
public class ServiceLocator {

    public static URL getServiceUrl(HttpServletRequest req) {

        String uri = null;
        try {
            uri = System.getProperty("service.url");
            if (uri != null) {
                return new URL(uri);
            }

            uri = System.getenv("SERVICE_URL");
            if (uri != null) {
                return new URL(uri);
            }

            String host = new URL(req.getRequestURL().toString()).getHost();
            if (host == null) {
                host = "localhost";
            }

            uri = "http://" + host + ":8080/service";
            return new URL(uri);

        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed url: " + uri);
        } finally {
            System.out.println("Service url: " + uri);
        }
    }
}
