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
import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { platformBrowser } from '@angular/platform-browser';

import { AppModule } from './app/app.module';

import { KeycloakService } from './app/keycloak-service/keycloak.service';

enableProdMode();

KeycloakService.init({ onLoad: 'check-sso', checkLoginIframeInterval: 1 })
    .then(() => {
        platformBrowserDynamic().bootstrapModule(AppModule);
    })
    .catch((e: string) => {
        console.log('Error in ng2 bootstrap: ' + e);
    });

