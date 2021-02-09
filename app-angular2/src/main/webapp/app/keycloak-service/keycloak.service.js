"use strict";
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
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.KeycloakService = void 0;
var core_1 = require("@angular/core");
var KeycloakService = /** @class */ (function () {
    function KeycloakService() {
    }
    KeycloakService_1 = KeycloakService;
    KeycloakService.init = function (options) {
        return new Promise(function (resolve, reject) {
            KeycloakService_1.keycloakAuth.init(options)
                .then(function () {
                resolve("success");
            })
                .catch(function (errorData) {
                reject(errorData);
            });
        });
    };
    KeycloakService.prototype.authenticated = function () {
        return KeycloakService_1.keycloakAuth.authenticated;
    };
    KeycloakService.prototype.login = function () {
        KeycloakService_1.keycloakAuth.login();
    };
    KeycloakService.prototype.logout = function () {
        KeycloakService_1.keycloakAuth.logout();
    };
    KeycloakService.prototype.account = function () {
        KeycloakService_1.keycloakAuth.accountManagement();
    };
    KeycloakService.prototype.getToken = function () {
        return new Promise(function (resolve, reject) {
            if (KeycloakService_1.keycloakAuth.token) {
                KeycloakService_1.keycloakAuth
                    .updateToken(5)
                    .then(function () {
                    resolve(KeycloakService_1.keycloakAuth.token);
                })
                    .catch(function () {
                    reject('Failed to refresh token');
                });
            }
            else {
                reject('Not loggen in');
            }
        });
    };
    var KeycloakService_1;
    // TODO: I have no idea how to import keycloak-js. I was always getting 404 error on 127.0.0.1:8080/app-angular2/keycloak-js so I worked it around with import in index.html
    // @ts-ignore
    KeycloakService.keycloakAuth = Keycloak();
    KeycloakService = KeycloakService_1 = __decorate([
        core_1.Injectable()
    ], KeycloakService);
    return KeycloakService;
}());
exports.KeycloakService = KeycloakService;
//# sourceMappingURL=keycloak.service.js.map