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

package org.keycloak.quickstart.writeable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.keycloak.common.util.EnvUtil;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PropertyFileUserStorageProvider implements
        UserStorageProvider,
        UserLookupProvider,
        CredentialInputValidator,
        CredentialInputUpdater,
        UserRegistrationProvider,
        UserQueryProvider {


    public static final String UNSET_PASSWORD="#$!-UNSET-PASSWORD";

    protected KeycloakSession session;
    protected Properties properties;
    protected ComponentModel model;
    // map of loaded users in this transaction
    protected Map<String, UserModel> loadedUsers = new HashMap<>();

    public PropertyFileUserStorageProvider(KeycloakSession session, ComponentModel model, Properties properties) {
        this.session = session;
        this.model = model;
        this.properties = properties;
    }

    // UserLookupProvider methods

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        UserModel adapter = loadedUsers.get(username);
        if (adapter == null) {
            String password = properties.getProperty(username);
            if (password != null) {
                adapter = createAdapter(realm, username);
                loadedUsers.put(username, adapter);
            }
        }
        return adapter;
    }
    

    protected UserModel createAdapter(RealmModel realm, String username) {
        return new AbstractUserAdapterFederatedStorage(session, realm, model) {
            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public void setUsername(String username) {
                String pw = (String)properties.remove(username);
                if (pw != null) {
                    properties.put(username, pw);
                    save();
                }
            }
        };
    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        StorageId storageId = new StorageId(id);
        String username = storageId.getExternalId();
        return getUserByUsername(realm, username);
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        return null;
    }

    // UserQueryProvider methods

    @Override
    public int getUsersCount(RealmModel realm) {
        return properties.size();
    }

    // UserQueryProvider method implementations

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, String search, Integer firstResult,
            Integer maxResults) {
        Predicate<String> predicate = "*".equals(search) ? username -> true : username -> username.contains(search);
        return properties.keySet().stream()
                .map(String.class::cast)
                .filter(predicate)
                .skip(firstResult)
                .map(username -> getUserByUsername(realm, username))
                .limit(maxResults);
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> params, Integer firstResult,
            Integer maxResults) {
        // only support searching by username
        String usernameSearchString = params.get("username");
        if (usernameSearchString != null)
            return searchForUserStream(realm, usernameSearchString, firstResult, maxResults);

        // if we are not searching by username, return all users
        return searchForUserStream(realm, "*", firstResult, maxResults);
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realm, GroupModel group, Integer firstResult, Integer maxResults) {
        // runtime automatically handles querying UserFederatedStorage
        return Stream.empty();
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realm, GroupModel group) {
        // runtime automatically handles querying UserFederatedStorage
        return Stream.empty();
    }

    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realm, String attrName, String attrValue) {
        // runtime automatically handles querying UserFederatedStorage
        return Stream.empty();
    }


    // UserRegistrationProvider method implementations

    public void save() {
        String path = model.getConfig().getFirst("path");
        path = EnvUtil.replace(path);
        try {
            FileOutputStream fos = new FileOutputStream(path);
            properties.store(fos, "");
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserModel addUser(RealmModel realm, String username) {
        synchronized (properties) {
            properties.setProperty(username, UNSET_PASSWORD);
            save();
        }
        return createAdapter(realm, username);
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        synchronized (properties) {
            if (properties.remove(user.getUsername()) == null) return false;
            save();
            return true;
        }
    }





    // CredentialInputValidator methods

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        String password = properties.getProperty(user.getUsername());
        return credentialType.equals(PasswordCredentialModel.TYPE) && password != null;
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return credentialType.equals(PasswordCredentialModel.TYPE);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) return false;

        UserCredentialModel cred = (UserCredentialModel)input;
        String password = properties.getProperty(user.getUsername());
        if (password == null || UNSET_PASSWORD.equals(password)) return false;
        return password.equals(cred.getValue());
    }

    // CredentialInputUpdater methods

    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        if (!(input instanceof UserCredentialModel)) return false;
        if (!input.getType().equals(PasswordCredentialModel.TYPE)) return false;
        UserCredentialModel cred = (UserCredentialModel)input;
        synchronized (properties) {
            properties.setProperty(user.getUsername(), cred.getValue());
            save();
        }
        return true;
    }

    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
        if (!credentialType.equals(PasswordCredentialModel.TYPE)) return;
        synchronized (properties) {
            properties.setProperty(user.getUsername(), UNSET_PASSWORD);
            save();
        }

    }

    private static final Set<String> disableableTypes = new HashSet<>();

    static {
        disableableTypes.add(PasswordCredentialModel.TYPE);
    }

    @Override
    public Stream<String> getDisableableCredentialTypesStream(RealmModel realm, UserModel user) {
        return disableableTypes.stream();
    }

    @Override
    public void close() {

    }
}