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
package org.keycloak.quickstart.actiontoken.reqaction;

import org.keycloak.Config;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.common.util.Time;
import org.keycloak.quickstart.actiontoken.token.ExternalApplicationNotificationActionToken;
import org.keycloak.quickstart.actiontoken.token.ExternalApplicationNotificationActionTokenHandler;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.Urls;
import org.keycloak.sessions.AuthenticationSessionModel;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.jboss.logging.Logger;

/**
 *
 * @author hmlnarik
 */
public class RedirectToExternalApplication implements RequiredActionProvider, RequiredActionFactory {

    private static final Logger logger = Logger.getLogger(RedirectToExternalApplication.class);

    public static final String ID = "redirect-to-external-application";

    public static final String DEFAULT_APPLICATION_ID = "application-id";

    private String externalApplicationUrl;

    private String applicationId;

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        int validityInSecs = context.getRealm().getActionTokenGeneratedByUserLifespan();
        int absoluteExpirationInSecs = Time.currentTime() + validityInSecs;
        final AuthenticationSessionModel authSession = context.getAuthenticationSession();
        final String clientId = authSession.getClient().getClientId();

        // Create a token used to return back to the current authentication flow
        String token = new ExternalApplicationNotificationActionToken(
          context.getUser().getId(),
          absoluteExpirationInSecs,
          clientId,
          applicationId
        ).serialize(
          context.getSession(),
          context.getRealm(),
          context.getUriInfo()
        );

        // This URL will be used by the application to submit the action token above to return back to the flow
        String submitActionTokenUrl;
        submitActionTokenUrl = Urls
          .actionTokenBuilder(context.getUriInfo().getBaseUri(), token, clientId, authSession.getTabId())
          .queryParam(ExternalApplicationNotificationActionTokenHandler.QUERY_PARAM_APP_TOKEN, "{tokenParameterName}")
          .build(context.getRealm().getName(), "{APP_TOKEN}")
          .toString();

        try {
            Response challenge = Response
              .status(Status.FOUND)
              .header("Location", externalApplicationUrl.replace("{TOKEN}", URLEncoder.encode(submitActionTokenUrl, "UTF-8")))
              .build();

            context.challenge(challenge);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }


    @Override
    public void processAction(RequiredActionContext context) {
        requiredActionChallenge(context);
    }


    @Override
    public void close() {

    }

    @Override
    public RequiredActionProvider create(KeycloakSession session) {
        return this;
    }

    @Override
    public void init(Config.Scope config) {
        this.externalApplicationUrl = config.get("external-application-url");
        if (this.externalApplicationUrl == null) {
            throw new RuntimeException("You have to set up external-application-url parameter in spi configuration with token position marked with \"{TOKEN}\" (without quotes) first.");
        }
        this.applicationId = config.get("application-id", DEFAULT_APPLICATION_ID);
        logger.infof("Using application ID: \"%s\"", this.applicationId);
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public String getDisplayText() {
        return "Redirect to external application";
    }

    @Override
    public String getId() {
        return ID;
    }
}
