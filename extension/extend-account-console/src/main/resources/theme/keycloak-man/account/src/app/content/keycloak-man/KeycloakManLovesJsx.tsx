/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { Component } from "react";

class KeycloakManLovesJsx extends Component {
  public render() {
    return (
      <div className="pf-c-card">
        <div className="pf-c-card__body">
          <div className="pf-c-empty-state pf-m-sm">
            <div className="pf-c-empty-state__content">
              <h4 className="pf-c-title pf-m-lg">
                Keycloak Man Loves JSX, React, and PatternFly
              </h4>
              <div className="pf-c-empty-state__body">
                <div className="pf-l-grid pf-m-gutter">
                  <div className="pf-l-grid__item pf-m-12-col">
                    <img src="public/keycloak-man-95x95.jpg" />
                  </div>
                  <div className="pf-l-grid__item pf-m-12-col">
                    <img src="public/heart-95x95.png" />
                  </div>
                  <div className="pf-l-grid__item pf-m-12-col">
                    <img src="public/jsx-95x95.png" />
                    <img src="public/react-95x95.png" />
                    <img src="public/patternfly-95x95.png" />
                  </div>
                </div>
              </div>
              <h4 className="pf-c-title pf-m-lg">
                But you can use whatever you want as long as you wrap it in a
                React Component.
              </h4>
            </div>
          </div>
        </div>
      </div>
    );
  }
}

export default KeycloakManLovesJsx;
