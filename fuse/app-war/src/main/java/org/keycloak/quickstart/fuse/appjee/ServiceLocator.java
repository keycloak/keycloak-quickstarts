/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

            URL requestUrl = new URL(req.getRequestURL().toString());

            String host = requestUrl.getHost();
            String schema = requestUrl.getProtocol();
            String port = requestUrl.getPort() != -1 ? (":" + requestUrl.getPort()) : "";

            uri = schema + "://" + host + port + "/service";
            return new URL(uri);

        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed url: " + uri);
        } finally {
            System.out.println("Service url: " + uri);
        }
    }
}
