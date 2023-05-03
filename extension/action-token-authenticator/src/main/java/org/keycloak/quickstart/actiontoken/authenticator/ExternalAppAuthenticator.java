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

import org.keycloak.TokenVerifier;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.common.VerificationException;
import org.keycloak.common.util.Time;
import org.keycloak.models.Constants;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.quickstart.actiontoken.token.ExternalApplicationNotificationActionToken;
import org.keycloak.quickstart.actiontoken.token.ExternalApplicationNotificationActionTokenHandler;
import org.keycloak.representations.JsonWebToken;
import org.keycloak.services.Urls;
import org.keycloak.services.messages.Messages;
import org.keycloak.sessions.AuthenticationSessionCompoundId;
import org.keycloak.sessions.AuthenticationSessionModel;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Objects;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.jboss.logging.Logger;
import static org.keycloak.quickstart.actiontoken.token.ExternalApplicationNotificationActionTokenHandler.QUERY_PARAM_APP_TOKEN;

/**
 *
 * @author hmlnarik
 */
public class ExternalAppAuthenticator implements Authenticator {

    private static final Logger logger = Logger.getLogger(ExternalAppAuthenticator.class);

    public static final String DEFAULT_EXTERNAL_APP_URL = "http://127.0.0.1:8080/action-token-responder-example/external-action.jsp?token={TOKEN}";

    public static final String DEFAULT_APPLICATION_ID = "application-id";

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        String externalApplicationUrl = null;
        String applicationId = null;
        if (context.getAuthenticatorConfig() != null) {
            externalApplicationUrl = context.getAuthenticatorConfig().getConfig().get(ExternalAppAuthenticatorFactory.CONFIG_EXTERNAL_APP_URL);
            applicationId = context.getAuthenticatorConfig().getConfig().get(ExternalAppAuthenticatorFactory.CONFIG_APPLICATION_ID);
        }
        if (externalApplicationUrl == null) {
            externalApplicationUrl = DEFAULT_EXTERNAL_APP_URL;
        }

        if (applicationId == null) {
            applicationId = DEFAULT_APPLICATION_ID;
        }

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
          .queryParam(Constants.EXECUTION, context.getExecution().getId())
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
    public void action(AuthenticationFlowContext context) {
        final AuthenticationSessionModel authSession = context.getAuthenticationSession();
        if (! Objects.equals(authSession.getAuthNote(ExternalApplicationNotificationActionTokenHandler.INITIATED_BY_ACTION_TOKEN_EXT_APP), "true")) {
            authenticate(context);
            return;
        }

        authSession.removeAuthNote(ExternalApplicationNotificationActionTokenHandler.INITIATED_BY_ACTION_TOKEN_EXT_APP);

        // Update user according to the claims in the application token
        String appTokenString = context.getUriInfo().getQueryParameters().getFirst(QUERY_PARAM_APP_TOKEN);
        UserModel user = authSession.getAuthenticatedUser();
        String applicationId = null;
        if (context.getAuthenticatorConfig() != null) {
            applicationId = context.getAuthenticatorConfig().getConfig().get(ExternalAppAuthenticatorFactory.CONFIG_APPLICATION_ID);
        }
        if (applicationId == null) {
            applicationId = DEFAULT_APPLICATION_ID;
        }

        try {
            JsonWebToken appToken = TokenVerifier.create(appTokenString, JsonWebToken.class).getToken();
            final String appId = applicationId;
            appToken.getOtherClaims()
              .forEach((key, value) -> user.setAttribute(appId + "." + key, Collections.singletonList(String.valueOf(value))));
        } catch (VerificationException ex) {
            logger.error("Error handling action token", ex);
            context.failure(AuthenticationFlowError.INTERNAL_ERROR, context.form()
                    .setError(Messages.INVALID_PARAMETER)
                    .createErrorPage(Status.INTERNAL_SERVER_ERROR));
        }

        context.success();
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
    }

    @Override
    public void close() {
    }

}
