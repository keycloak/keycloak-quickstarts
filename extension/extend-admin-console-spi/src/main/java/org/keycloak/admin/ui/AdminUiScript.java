/*
 * Copyright 2026 Red Hat, Inc. and/or its affiliates
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
package org.keycloak.admin.ui;

import org.keycloak.Config;
import org.keycloak.common.Version;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.ui.extend.UiScriptProvider;
import org.keycloak.services.ui.extend.UiScriptProviderFactory;

/**
 * Adds a "Hello" item under Configure in the admin console. Clicking it loads
 * the ES module from {@link #getUrl()} and mounts the returned element.
 */
public class AdminUiScript implements UiScriptProvider, UiScriptProviderFactory {

    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return "Hello";
    }

    @Override
    public String getUrl() {
        // Served from theme-resources/resources/content/hello-realm.js in this JAR
        return "/resources/" + Version.RESOURCES_VERSION + "/admin/keycloak.v2/content/hello-realm.js";
    }
}
