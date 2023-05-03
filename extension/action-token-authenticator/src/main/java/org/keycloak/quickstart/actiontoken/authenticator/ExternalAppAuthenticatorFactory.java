/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates
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
package org.keycloak.quickstart.actiontoken.authenticator;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import java.util.Arrays;
import java.util.List;
import static org.keycloak.provider.ProviderConfigProperty.STRING_TYPE;

/**
 *
 * @author hmlnarik
 */
public class ExternalAppAuthenticatorFactory implements AuthenticatorFactory {

    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.DISABLED
    };

    public static final String CONFIG_APPLICATION_ID = "application-id";
    public static final String CONFIG_EXTERNAL_APP_URL = "external-application-url";

    public static final String ID = "external-application-authenticator";

    @Override
    public String getDisplayType() {
        return "External Application Authenticator";
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return "External Application Authenticator";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        ProviderConfigProperty rep1 = new ProviderConfigProperty(CONFIG_APPLICATION_ID, "Application ID",
          "Application ID sent in the token",
          STRING_TYPE, ExternalAppAuthenticator.DEFAULT_APPLICATION_ID);

        ProviderConfigProperty rep2 = new ProviderConfigProperty(CONFIG_EXTERNAL_APP_URL, "External Application URL",
          "URL of the application to redirect to. It has to contain token position marked with \"{TOKEN}\" (without quotes).",
          STRING_TYPE, ExternalAppAuthenticator.DEFAULT_EXTERNAL_APP_URL);
        
        return Arrays.asList(rep1, rep2);
    }

    @Override
    public Authenticator create(KeycloakSession keycloakSession) {
        return new ExternalAppAuthenticator();
    }

    @Override
    public void init(Config.Scope scope) {
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return ID;
    }
}
