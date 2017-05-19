/*
 * JBoss, Home of Professional Open Source
 *
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.keycloak.quickstart.springboot.security;

import java.util.List;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.idm.authorization.Permission;

/**
 * This is a simple facade to obtain information from authenticated users.
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class Identity {

    private final KeycloakSecurityContext securityContext;

    public Identity(KeycloakSecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    public boolean hasResourcePermission(String name) {
        return this.securityContext.getAuthorizationContext().hasResourcePermission(name);
    }

    public String getName() {
        return this.securityContext.getIdToken().getPreferredUsername();
    }

    public List<Permission> getPermissions() {
        return this.securityContext.getAuthorizationContext().getPermissions();
    }
}
