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
import { Component } from '@angular/core';
import {Http, Headers, RequestOptions, Response} from '@angular/http';

import {KeycloakService} from './keycloak-service/keycloak.service';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  private serviceUrl: string = 'http://127.0.0.1:8080/service/';

  public message: string;
  public errorClass: string = '';

  constructor(private http: Http, private kc: KeycloakService) {
  }

  authenticated(): boolean {
    return this.kc.authenticated();
  }

  login() {
    this.kc.login();
  }

  logout() {
    this.kc.logout();
  }

  account() {
    this.kc.account();
  }

  request(endpoint: string) {
    this.http.get(this.serviceUrl + endpoint)
        .subscribe((res: Response) => this.handleResponse(res, this),
                   (error: Response) => this.handleServiceError(error, this));
  }

  private handleResponse(res: Response, comp: AppComponent) {
    comp.errorClass = '';
    comp.message = 'Message: ' + res.json().message;
  }

  private handleServiceError(error: Response, comp: AppComponent) {
    comp.errorClass = 'error';
    if (error.status === 0) {
      comp.message = 'Request failed';
    } else {
      comp.message = error.status + ' ' + error.statusText;
    }
  }
}
