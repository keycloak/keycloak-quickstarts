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

package org.keycloak.quickstart;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

public class PageHelper {

    private final static Logger log = Logger.getLogger(PageHelper.class);

    @Drone
    private WebDriver browser;

    public boolean isOnStartPage() {
        fixStylesPage();
        return browser.findElements(By.name("loginBtn")).size() > 0;
    }

    public void waitForAccountBtn() {
        fixStylesPage();
        try {
            Graphene.waitAjax().until().element(By.name("accountBtn")).is().present();
        } catch (TimeoutException ex) {
            throw new TimeoutException("timeout on page " + browser.getCurrentUrl(), ex);
        }
    }

    private void fixStylesPage() {
        if (browser.getCurrentUrl().endsWith("/styles.css")) {
            String newUrl = browser.getCurrentUrl().replaceAll("styles.css$", "");
            log.info("redirecting browser from " + browser.getCurrentUrl() + " to " + newUrl + " (might be a phantomJS bug)");
            browser.get(newUrl);
        }
    }

    public void isOnLoginPage() {
        try {
            Graphene.waitAjax().until().element(By.id("username")).is().present();
        } catch (TimeoutException ex) {
            throw new TimeoutException("timeout on page " + browser.getCurrentUrl(), ex);
        }
    }
}
