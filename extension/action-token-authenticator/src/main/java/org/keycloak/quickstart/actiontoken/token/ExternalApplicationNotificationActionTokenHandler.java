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

import org.keycloak.Config.Scope;
import org.keycloak.TokenVerifier;
import org.keycloak.TokenVerifier.Predicate;
import org.keycloak.authentication.AuthenticationProcessor;
import org.keycloak.authentication.actiontoken.AbstractActionTokenHandler;
import org.keycloak.authentication.actiontoken.ActionTokenContext;
import org.keycloak.authentication.actiontoken.TokenUtils;
import org.keycloak.common.VerificationException;
import org.keycloak.common.util.Base64;
import org.keycloak.events.Errors;
import org.keycloak.events.EventType;
import org.keycloak.representations.JsonWebToken;
import org.keycloak.services.messages.Messages;
import org.keycloak.sessions.AuthenticationSessionCompoundId;
import org.keycloak.sessions.AuthenticationSessionModel;
import java.io.IOException;
import javax.crypto.spec.SecretKeySpec;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import static org.keycloak.services.resources.LoginActionsService.AUTHENTICATE_PATH;

/**
 * Action token handler for verification of e-mail address.
 * @author hmlnarik
 */
public class ExternalApplicationNotificationActionTokenHandler extends AbstractActionTokenHandler<ExternalApplicationNotificationActionToken> {

    public static final String QUERY_PARAM_APP_TOKEN = "app-token";

    public static final String INITIATED_BY_ACTION_TOKEN_EXT_APP = "INITIATED_BY_ACTION_TOKEN_EXT_APP";

    private SecretKeySpec hmacSecretKeySpec = null;

    public ExternalApplicationNotificationActionTokenHandler() {
        super(
          ExternalApplicationNotificationActionToken.TOKEN_TYPE,
          ExternalApplicationNotificationActionToken.class,
          Messages.INVALID_REQUEST,
          EventType.EXECUTE_ACTION_TOKEN,
          Errors.INVALID_REQUEST
        );
    }

    private boolean isApplicationTokenValid(
      ExternalApplicationNotificationActionToken token,
      ActionTokenContext<ExternalApplicationNotificationActionToken> tokenContext
    ) throws VerificationException {
        String appTokenString = tokenContext.getUriInfo().getQueryParameters().getFirst(QUERY_PARAM_APP_TOKEN);

        TokenVerifier.create(appTokenString, JsonWebToken.class)
          .secretKey(hmacSecretKeySpec)
          .verify();

        return true;
    }

    @Override
    public Predicate<? super ExternalApplicationNotificationActionToken>[] getVerifiers(ActionTokenContext<ExternalApplicationNotificationActionToken> tokenContext) {
        return TokenUtils.predicates(
          // Check that the app token is set in query parameters
          t -> tokenContext.getUriInfo().getQueryParameters().getFirst(QUERY_PARAM_APP_TOKEN) != null,

          // Validate correctness of the app token
          t -> isApplicationTokenValid(t, tokenContext)
        );
    }

    @Override
    public Response handleToken(ExternalApplicationNotificationActionToken token, ActionTokenContext<ExternalApplicationNotificationActionToken> tokenContext) {
        // Continue with the authenticator action
        tokenContext.getAuthenticationSession().setAuthNote(INITIATED_BY_ACTION_TOKEN_EXT_APP, "true");
        return tokenContext.processFlow(true, AUTHENTICATE_PATH, tokenContext.getRealm().getBrowserFlow(), null, new AuthenticationProcessor());
    }

    private static final Logger LOG = Logger.getLogger(ExternalApplicationNotificationActionTokenHandler.class);

    @Override
    public String getAuthenticationSessionIdFromToken(ExternalApplicationNotificationActionToken token, ActionTokenContext<ExternalApplicationNotificationActionToken> tokenContext,
      AuthenticationSessionModel currentAuthSession) {
        // always join current authentication session
        final String id = currentAuthSession == null
          ? null
          : AuthenticationSessionCompoundId.fromAuthSession(currentAuthSession).getEncodedId();

        LOG.infof("Returning %s", id);

        return id;
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
