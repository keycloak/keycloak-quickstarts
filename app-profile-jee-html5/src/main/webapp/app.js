/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
var keycloak = new Keycloak();

function welcome() {
    show('welcome');
}

function showProfile() {
    if (keycloak.tokenParsed['given_name']) document.getElementById('firstName').innerHTML = keycloak.tokenParsed['given_name'];
    if (keycloak.tokenParsed['family_name']) document.getElementById('lastName').innerHTML = keycloak.tokenParsed['family_name'];
    if (keycloak.tokenParsed['preferred_username']) document.getElementById('username').innerHTML = keycloak.tokenParsed['preferred_username'];
    if (keycloak.tokenParsed['email']) document.getElementById('email').innerHTML = keycloak.tokenParsed['email'];
    show('profile');
}

function showToken() {
    document.getElementById('token-content').innerHTML = JSON.stringify(keycloak.tokenParsed, null, '    ');
    show('token');
}

function show(id) {
    document.getElementById('welcome').style.display = 'none';
    document.getElementById('profile').style.display = 'none';
    document.getElementById('token').style.display = 'none';
    document.getElementById(id).style.display = 'block';
}

keycloak.onAuthLogout = welcome;

window.onload = function () {
    keycloak.init({ onLoad: 'check-sso', checkLoginIframeInterval: 1 }).success(function () {
        if (keycloak.authenticated) {
            showProfile();
        } else {
            welcome();
        }

        document.body.style.display = 'block';
    });
}
