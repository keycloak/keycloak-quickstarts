package org.keycloak.quickstart.fuse.appjee;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.util.JsonSerialization;

/**
 * Client that calls the service.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2015 Red Hat Inc.
 */
public class ServiceClient {

    public static class MessageBean {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class Failure extends Exception {
        private int status;
        private String reason;
        private String requestUri;

        public Failure(int status, String reason, String requestUri) {
            this.status = status;
            this.reason = reason;
            this.requestUri = requestUri;
        }

        public int getStatus() {
            return status;
        }

        public String getReason() {
            return reason;
        }

        public String getRequestUri() {
            return requestUri;
        }
    }

    private static String getServiceUrl(HttpServletRequest req) {
        return ServiceLocator.getServiceUrl(req).toExternalForm();
    }

    public static CloseableHttpClient createHttpClient() {
        return HttpClients.createDefault();
    }

    public static String callService(HttpServletRequest req, KeycloakSecurityContext session, String action) throws Failure {
        CloseableHttpClient client = null;
        try {
            client = createHttpClient();
            String requestUri = getServiceUrl(req) + "/" + action;
            HttpGet get = new HttpGet(requestUri);
            if (session != null) {
                get.addHeader("Authorization", "Bearer " + session.getTokenString());
            }

            HttpResponse response = client.execute(get);

            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() != 200) {
                throw new Failure(status.getStatusCode(), status.getReasonPhrase(), requestUri);
            }

            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            try {
                MessageBean message = JsonSerialization.readValue(is, MessageBean.class);
                return message.getMessage();
            } finally {
                is.close();
            }
        } catch (Failure f) {
            throw f;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (client != null)
                try {
                    client.close();
                } catch (Exception e) {
                    throw new RuntimeException("Error while closing HttpClient", e);
                }
        }
    }
}
