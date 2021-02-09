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

import {Injectable} from '@angular/core';
import {KeycloakInstance} from "keycloak-js";


@Injectable()
export class KeycloakService {
    // TODO: remove ts-ignore and import Keycloak.
    //  I have no idea how to import keycloak-js. I was always getting 404 error on 127.0.0.1:8080/app-angular2/keycloak-js so I worked it around with an import in index.html
    // @ts-ignore
    static keycloakAuth: KeycloakInstance = Keycloak();

    static init(options?: any): Promise<any> {
        return new Promise((resolve, reject) => {
            KeycloakService.keycloakAuth.init(options)
                .then(() => {
                    resolve("success");
                })
                .catch((errorData: any) => {
                    reject(errorData);
                });
        });
    }

    authenticated(): boolean {
        return KeycloakService.keycloakAuth.authenticated;
    }

    login() {
        KeycloakService.keycloakAuth.login();
    }

    logout() {
        KeycloakService.keycloakAuth.logout();
    }

    account() {
        KeycloakService.keycloakAuth.accountManagement();
    }

    getToken(): Promise<string> {
        return new Promise<string>((resolve, reject) => {
            if (KeycloakService.keycloakAuth.token) {
                KeycloakService.keycloakAuth
                    .updateToken(5)
                    .then(() => {
                        resolve(<string>KeycloakService.keycloakAuth.token);
                    })
                    .catch(() => {
                        reject('Failed to refresh token');
                    });
            } else {
                reject('Not loggen in');
            }
        });
    }
}
