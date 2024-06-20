/*
 * Copyright 2024 Red Hat, Inc. and/or its affiliates
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
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.services.ui.extend.UiPageProvider;
import org.keycloak.services.ui.extend.UiPageProviderFactory;

import java.util.List;

/**
 * Implements UiPageProvider so it will be a master detail view in the admin ui of TODO items
 */
public class AdminUiPage implements UiPageProvider, UiPageProviderFactory<ComponentModel> {

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
        return "Todo";
    }

    @Override
    public String getHelpText() {
        return "Here you can store your Todo items";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create()
                .property()
                .name("name")
                .label("Name")
                .helpText("Short name of the task")
                .type(ProviderConfigProperty.STRING_TYPE)
                .add().property()
                .name("description")
                .label("Description")
                .helpText("Description of what needs to be done")
                .type(ProviderConfigProperty.TEXT_TYPE)
                .add().property()
                .name("prio")
                .label("Priority")
                .type(ProviderConfigProperty.LIST_TYPE)
                .options("critical", "high priority", "neutral", "low priority", "unknown")
                .add().build();
    }
}
