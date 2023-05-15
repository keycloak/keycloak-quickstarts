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
package org.keycloak.quickstart.actiontoken.token;

import org.jboss.logging.Logger;
import org.keycloak.Config.Scope;
import org.keycloak.TokenVerifier;
import org.keycloak.TokenVerifier.Predicate;
import org.keycloak.authentication.actiontoken.AbstractActionTokenHandler;
import org.keycloak.authentication.actiontoken.ActionTokenContext;
import org.keycloak.authentication.actiontoken.TokenUtils;
import org.keycloak.common.VerificationException;
import org.keycloak.common.util.Base64;
import org.keycloak.events.Errors;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.UserModel;
import org.keycloak.quickstart.actiontoken.reqaction.RedirectToExternalApplication;
import org.keycloak.representations.JsonWebToken;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.managers.AuthenticationSessionManager;
import org.keycloak.services.messages.Messages;
import org.keycloak.sessions.AuthenticationSessionCompoundId;
import org.keycloak.sessions.AuthenticationSessionModel;

import javax.crypto.spec.SecretKeySpec;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.util.Collections;

/**
 * Action token handler for verification of e-mail address.
 * @author hmlnarik
 */
public class ExternalApplicationNotificationReqactionActionTokenHandler extends AbstractActionTokenHandler<ExternalApplicationNotificationReqactionActionToken> {

    public static final String QUERY_PARAM_APP_TOKEN = "app-token-reqaction";

    private SecretKeySpec hmacSecretKeySpec = null;

    public ExternalApplicationNotificationReqactionActionTokenHandler() {
        super(
          ExternalApplicationNotificationReqactionActionToken.TOKEN_TYPE,
          ExternalApplicationNotificationReqactionActionToken.class,
          Messages.INVALID_REQUEST,
          EventType.EXECUTE_ACTION_TOKEN,
          Errors.INVALID_REQUEST
        );
    }

    private boolean isApplicationTokenValid(
      ExternalApplicationNotificationReqactionActionToken token,
      ActionTokenContext<ExternalApplicationNotificationReqactionActionToken> tokenContext
    ) throws VerificationException {
        String appTokenString = tokenContext.getUriInfo().getQueryParameters().getFirst(QUERY_PARAM_APP_TOKEN);

        TokenVerifier.create(appTokenString, JsonWebToken.class)
          .secretKey(hmacSecretKeySpec)
          .verify();

        return true;
    }

    @Override
    public Predicate<? super ExternalApplicationNotificationReqactionActionToken>[] getVerifiers(ActionTokenContext<ExternalApplicationNotificationReqactionActionToken> tokenContext) {
        return TokenUtils.predicates(
          // Check that the app token is set in query parameters
          t -> tokenContext.getUriInfo().getQueryParameters().getFirst(QUERY_PARAM_APP_TOKEN) != null,

          // Validate correctness of the app token
          t -> isApplicationTokenValid(t, tokenContext)
        );
    }

    private static final Logger LOG = Logger.getLogger(ExternalApplicationNotificationReqactionActionTokenHandler.class);

    @Override
    public String getAuthenticationSessionIdFromToken(ExternalApplicationNotificationReqactionActionToken token, ActionTokenContext<ExternalApplicationNotificationReqactionActionToken> tokenContext,
                                                      AuthenticationSessionModel currentAuthSession) {
        // always join current authentication session
        final String id = currentAuthSession == null
          ? null
          : AuthenticationSessionCompoundId.fromAuthSession(currentAuthSession).getEncodedId();

        LOG.infof("Returning %s", id);

        return id;
    }

    @Override
    public Response handleToken(ExternalApplicationNotificationReqactionActionToken token, ActionTokenContext<ExternalApplicationNotificationReqactionActionToken> tokenContext) {
        UserModel user = tokenContext.getAuthenticationSession().getAuthenticatedUser();
        EventBuilder event = tokenContext.getEvent();

        AuthenticationSessionModel authSession = tokenContext.getAuthenticationSession();

        // Update user according to the claims in the application token
        String appTokenString = tokenContext.getUriInfo().getQueryParameters().getFirst(QUERY_PARAM_APP_TOKEN);
        try {
            JsonWebToken appToken = TokenVerifier.create(appTokenString, JsonWebToken.class).getToken();
            appToken.getOtherClaims()
              .forEach((key, value) -> user.setAttribute(token.getApplicationId() + "." + key, Collections.singletonList(String.valueOf(value))));
        } catch (VerificationException ex) {
            return tokenContext.getSession().getProvider(LoginFormsProvider.class)
                    .setError(Messages.INVALID_PARAMETER)
                    .createErrorPage(Response.Status.INTERNAL_SERVER_ERROR);
        }

        event.success();

        user.removeRequiredAction(RedirectToExternalApplication.ID);
        authSession.removeRequiredAction(RedirectToExternalApplication.ID);

        // User updated. Now if authentication is not in progress (e.g. opened in a new browser window), just show an info that account has been updated
        if (tokenContext.isAuthenticationSessionFresh()) {
            AuthenticationSessionManager asm = new AuthenticationSessionManager(tokenContext.getSession());
            asm.removeAuthenticationSession(tokenContext.getRealm(), authSession, true);
            return tokenContext.getSession().getProvider(LoginFormsProvider.class)
                    .setSuccess(Messages.ACCOUNT_UPDATED)
                    .createInfoPage();
        }

        // Otherwise continue with next required action (if any)
        String nextAction = AuthenticationManager.nextRequiredAction(tokenContext.getSession(), authSession, tokenContext.getRequest(), event);
        return AuthenticationManager.redirectToRequiredActions(tokenContext.getSession(), tokenContext.getRealm(), authSession, tokenContext.getUriInfo(), nextAction);
    }

    @Override
    public void init(Scope config) {
        final String secret = config.get("hmacSecret", null);

        if (secret == null) {
            throw new RuntimeException("You have to configure HMAC secret");
        }

        try {
            this.hmacSecretKeySpec = new SecretKeySpec(Base64.decode(secret), "HmacSHA256");
        } catch (IOException ex) {
            throw new RuntimeException("Cannot decode HMAC secret from string", ex);
        }
    }
}
