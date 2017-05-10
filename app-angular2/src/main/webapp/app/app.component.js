"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
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
var core_1 = require("@angular/core");
var http_1 = require("@angular/http");
var keycloak_service_1 = require("./keycloak-service/keycloak.service");
require("rxjs/add/operator/catch");
require("rxjs/add/operator/map");
var AppComponent = (function () {
    function AppComponent(http, kc) {
        this.http = http;
        this.kc = kc;
        this.serviceUrl = 'http://127.0.0.1:8080/service/';
        this.errorClass = '';
    }
    AppComponent.prototype.authenticated = function () {
        return this.kc.authenticated();
    };
    AppComponent.prototype.login = function () {
        this.kc.login();
    };
    AppComponent.prototype.logout = function () {
        this.kc.logout();
    };
    AppComponent.prototype.account = function () {
        this.kc.account();
    };
    AppComponent.prototype.request = function (endpoint) {
        var _this = this;
        this.http.get(this.serviceUrl + endpoint)
            .subscribe(function (res) { return _this.handleResponse(res, _this); }, function (error) { return _this.handleServiceError(error, _this); });
    };
    AppComponent.prototype.handleResponse = function (res, comp) {
        comp.errorClass = '';
        comp.message = 'Message: ' + res.json().message;
    };
    AppComponent.prototype.handleServiceError = function (error, comp) {
        comp.errorClass = 'error';
        if (error.status === 0) {
            comp.message = 'Request failed';
        }
        else {
            comp.message = error.status + ' ' + error.statusText;
        }
    };
    return AppComponent;
}());
AppComponent = __decorate([
    core_1.Component({
        selector: 'app-root',
        templateUrl: './app.component.html',
        styleUrls: ['./app.component.css']
    }),
    __metadata("design:paramtypes", [http_1.Http, keycloak_service_1.KeycloakService])
], AppComponent);
exports.AppComponent = AppComponent;
//# sourceMappingURL=app.component.js.map