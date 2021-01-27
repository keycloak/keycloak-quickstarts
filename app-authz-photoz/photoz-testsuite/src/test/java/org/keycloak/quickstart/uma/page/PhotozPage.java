/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates
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

package org.keycloak.quickstart.uma.page;

import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.keycloak.test.page.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.jboss.arquillian.graphene.Graphene.waitGui;

/**
 * A {@code {@link Page}} representing the Photoz application.
 *
 * @author <a href="mailto:sguilhen@redhat.com">Stefan Guilhen</a>
 */
public class PhotozPage {

    protected final static org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(PhotozPage.class);

    public static final String PAGELOAD_TIMEOUT_PROP = "pageload.timeout";

    public static final Integer PAGELOAD_TIMEOUT_MILLIS = Integer.parseInt(System.getProperty(PAGELOAD_TIMEOUT_PROP, "10000"));

    @ArquillianResource
    protected WebDriver webDriver;

    @Page
    private LoginPage loginPage;

    @Page
    private ConsentPage consentPage;

    @FindBy(xpath = "//a[@ng-click = 'Identity.logout()']")
    private WebElement signOutLink;

    @FindBy(xpath = "//a[@ng-click = 'Identity.account()']")
    private WebElement myAccountLink;

    @FindBy(xpath = "//a[@data-ng-click = 'requestEntitlements()']")
    private WebElement requestEntitlementsLink;

    @FindBy(tagName = "h2")
    private WebElement welcomeMessage;

    @FindBy(id = "output")
    private WebElement output;


    public void login(final String username, final String password, final String fullName) {
        loginPage.login(username, password);
        if (consentPage.isCurrent()) {
            consentPage.confirm();
        }
        waitGui().until().element(welcomeMessage).is().visible();
        if (fullName != null) {
            Assert.assertEquals("Welcome To Photoz, " + fullName, welcomeMessage.getText());
        }
    }

    public void logout() {
        waitGui().until().element(signOutLink).is().clickable();
        signOutLink.click();
    }

    public void createAlbum(final String albumName) {
        WebElement createAlbum = webDriver.findElement(By.id("create-album"));
        waitGui().until().element(createAlbum).is().clickable();
        createAlbum.click();
        WebElement albumNameInput = webDriver.findElement(By.id("album.name"));
        waitGui().until().element(albumNameInput).is().present();
        albumNameInput.sendKeys(albumName);
        waitGui().until().element(albumNameInput).attribute("value").contains(albumName);
        WebElement saveButton = webDriver.findElement(By.id("save-album"));
        waitGui().until().element(saveButton).is().clickable();
        saveButton.click();
        waitGui().until().element(albumNameInput).is().not().present();
    }

    public void deleteAlbum(final String albumName) {
        this.deleteAlbum(webDriver.findElement(By.id("delete-" + albumName)));
    }

    public void deleteSharedAlbum(final String albumName) {
        this.deleteAlbum(webDriver.findElement(By.id("delete-share-" + albumName)));
    }

    public void deleteSharedAlbum(final String albumName, boolean waitForRemoval) {
        this.deleteAlbum(webDriver.findElement(By.id("delete-share-" + albumName)), waitForRemoval);
    }

    private void deleteAlbum(final WebElement deleteLink) {
        deleteAlbum(deleteLink, true);
    }

    private void deleteAlbum(final WebElement deleteLink, boolean waitForRemoval) {
        waitGui().until().element(deleteLink).is().clickable();
        deleteLink.click();
        if (waitForRemoval) {
            waitGui().until().element(deleteLink).is().not().present();
        }
    }

    public boolean wasDenied() {
        try {
            waitGui().until().element(By.id("output")).text().matches("You can not access or perform the requested operation on this resource.");
        }
        catch (TimeoutException e) {
            return false;
        }
        return true;
    }

}
