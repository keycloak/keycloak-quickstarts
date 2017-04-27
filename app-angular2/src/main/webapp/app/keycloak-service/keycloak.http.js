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
"use strict";
var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var core_1 = require("@angular/core");
var http_1 = require("@angular/http");
var keycloak_service_1 = require("./keycloak.service");
var Rx_1 = require("rxjs/Rx");
/**
 * This provides a wrapper over the ng2 Http class that insures tokens are refreshed on each request.
 */
var KeycloakHttp = (function (_super) {
    __extends(KeycloakHttp, _super);
    function KeycloakHttp(_backend, _defaultOptions, _keycloakService) {
        var _this = _super.call(this, _backend, _defaultOptions) || this;
        _this._keycloakService = _keycloakService;
        return _this;
    }
    KeycloakHttp.prototype.request = function (url, options) {
        var _this = this;
        if (!this._keycloakService.authenticated())
            return _super.prototype.request.call(this, url, options);
        var tokenPromise = this._keycloakService.getToken();
        var tokenObservable = Rx_1.Observable.fromPromise(tokenPromise);
        if (typeof url === 'string') {
            return tokenObservable.map(function (token) {
                var authOptions = new http_1.RequestOptions({ headers: new http_1.Headers({ 'Authorization': 'Bearer ' + token }) });
                return new http_1.RequestOptions().merge(options).merge(authOptions);
            }).concatMap(function (opts) { return _super.prototype.request.call(_this, url, opts); });
        }
        else if (url instanceof http_1.Request) {
            return tokenObservable.map(function (token) {
                url.headers.set('Authorization', 'Bearer ' + token);
                return url;
            }).concatMap(function (request) { return _super.prototype.request.call(_this, request); });
        }
    };
    return KeycloakHttp;
}(http_1.Http));
KeycloakHttp = __decorate([
    core_1.Injectable(),
    __metadata("design:paramtypes", [http_1.ConnectionBackend, http_1.RequestOptions, keycloak_service_1.KeycloakService])
], KeycloakHttp);
exports.KeycloakHttp = KeycloakHttp;
function keycloakHttpFactory(backend, defaultOptions, keycloakService) {
    return new KeycloakHttp(backend, defaultOptions, keycloakService);
}
exports.keycloakHttpFactory = keycloakHttpFactory;
exports.KEYCLOAK_HTTP_PROVIDER = {
    provide: http_1.Http,
    useFactory: keycloakHttpFactory,
    deps: [http_1.XHRBackend, http_1.RequestOptions, keycloak_service_1.KeycloakService]
};
//# sourceMappingURL=keycloak.http.js.map