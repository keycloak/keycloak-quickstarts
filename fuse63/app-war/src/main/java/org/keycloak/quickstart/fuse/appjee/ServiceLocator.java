package org.keycloak.quickstart.fuse.appjee;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="mailto:mstrukel@redhat.com">Marko Strukelj</a>
 */
public class ServiceLocator {

    private static final Logger log = Logger.getLogger(ServiceLocator.class.getName());

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

            URL requestUrl = new URL(req.getRequestURL().toString());

            String host = requestUrl.getHost();
            String schema = requestUrl.getProtocol();
            String port = requestUrl.getPort() != -1 ? (":" + requestUrl.getPort()) : "";

            uri = schema + "://" + host + port + "/service";
            return new URL(uri);

        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed url: " + uri);
        } finally {
            log.info("Service url: " + uri);
        }
    }
}
