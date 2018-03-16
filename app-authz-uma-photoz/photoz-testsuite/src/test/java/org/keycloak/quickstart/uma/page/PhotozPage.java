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

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.keycloak.test.page.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.jboss.arquillian.graphene.Graphene.waitGui;
import static org.openqa.selenium.support.ui.ExpectedConditions.javaScriptThrowsNoExceptions;
import static org.openqa.selenium.support.ui.ExpectedConditions.not;
import static org.openqa.selenium.support.ui.ExpectedConditions.urlToBe;

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

    @FindBy(id = "referrer")
    private WebElement backToAppLink;


    public void login(final String username, final String password, final String fullName) {
        loginPage.login(username, password);
        waitForPageToLoad();
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
        waitForPageToLoad();
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

    private void deleteAlbum(final WebElement deleteLink) {
        waitGui().until().element(deleteLink).is().clickable();
        deleteLink.click();
        waitGui().until().element(deleteLink).is().not().present();
        waitForPageToLoad();
    }

    public void requestEntitlements() {
        waitGui().until().element(requestEntitlementsLink).is().clickable();
        requestEntitlementsLink.click();
        waitGui().until().element(output).text().not().equalTo("");
    }

    public void shareResource(String resource, String user) {
        goToAccountMyResource(resource);
        WebElement userIdInput = webDriver.findElement(By.id("user_id"));
        waitGui().until().element(userIdInput).is().present();
        userIdInput.sendKeys(user);
        waitGui().until().element(userIdInput).attribute("value").contains(user);

        WebElement shareButton = webDriver.findElement(By.id("share-button"));
        waitGui().until().element(shareButton).is().clickable();
        shareButton.click();
        waitForPageToLoad();
        waitGui().until().element(backToAppLink).is().clickable();
        backToAppLink.click();
        waitForPageToLoad();
    }

    public void shareResourceWithExcludedScope(String resource, String user, String scope) {
        goToAccountMyResource(resource);
        WebElement userIdInput = webDriver.findElement(By.id("user_id"));
        waitGui().until().element(userIdInput).is().present();
        userIdInput.sendKeys(user);
        waitGui().until().element(userIdInput).attribute("value").contains(user);

        WebElement shareRemoveScope = webDriver.findElement(By.id("share-remove-scope-" + resource + "-" + scope));
        waitGui().until().element(shareRemoveScope).is().clickable();
        shareRemoveScope.click();
        waitForPageToLoad();

        WebElement shareButton = webDriver.findElement(By.id("share-button"));
        waitGui().until().element(shareButton).is().clickable();
        shareButton.click();
        waitForPageToLoad();
        waitGui().until().element(backToAppLink).is().clickable();
        backToAppLink.click();
        waitForPageToLoad();
    }

    public void requestDeleteScope(String albumName) {
        WebElement requestDeleteAccessLink = webDriver.findElement(By.id("request-delete-share-" + albumName));
        waitGui().until().element(requestDeleteAccessLink).is().clickable();
        requestDeleteAccessLink.click();
        waitGui().until().element(output).text().not().equalTo("");
    }

    public void grantRequestedPermission(String resource, String requester) {
        goToAccountMyResources();
        WebElement grantRemoveScope = webDriver.findElement(By.id("grant-" + resource + "-" + requester));
        waitGui().until().element(grantRemoveScope).is().clickable();
        grantRemoveScope.click();
        waitForPageToLoad();
        waitGui().until().element(backToAppLink).is().clickable();
        backToAppLink.click();
        waitForPageToLoad();
    }

    public void goToAccountMyResource(String name) {
        goToAccountMyResources();
        WebElement myResource = webDriver.findElement(By.id("detail-" + name));
        waitGui().until().element(myResource).is().clickable();
        myResource.click();
        waitForPageToLoad();
    }

    public void goToAccountMyResources() {
        gotToAccountPage();
        WebElement myResources = webDriver.findElement(By.xpath("//a[text() = 'My Resources']"));
        waitGui().until().element(myResources).is().clickable();
        myResources.click();
        waitForPageToLoad();
    }

    public void gotToAccountPage() {
        waitGui().until().element(myAccountLink).is().clickable();
        myAccountLink.click();
        waitForPageToLoad();
    }

    public void waitForPageToLoad() {

        if (webDriver instanceof HtmlUnitDriver) {
            return; // not needed
        }

        // Ensure the URL is "stable", i.e. is not changing anymore; if it'd changing, some redirects are probably still in progress
        for (int maxRedirects = 2; maxRedirects > 0; maxRedirects--) {
            String currentUrl = webDriver.getCurrentUrl();
            FluentWait<WebDriver> wait = new FluentWait<>(webDriver).withTimeout(Duration.of(250, ChronoUnit.MILLIS));
            try {
                wait.until(not(urlToBe(currentUrl)));
            } catch (TimeoutException e) {
                break; // URL has not changed recently - ok, the URL is stable and page is current
            }
            if (maxRedirects == 1) {
                log.warn("URL seems unstable! (Some redirect are probably still in progress)");
            }
        }

        WebDriverWait wait = new WebDriverWait(webDriver, PAGELOAD_TIMEOUT_MILLIS / 1000);

        try {
            // Checks if the document is ready and asks AngularJS, if present, whether there are any REST API requests
            // in progress
            wait.until(javaScriptThrowsNoExceptions(
                    "if (document.readyState !== 'complete' "
                            + "|| (typeof angular !== 'undefined' && angular.element(document.body).injector().get('$http').pendingRequests.length !== 0)) {"
                            + "throw \"Not ready\";"
                            + "}"));
        } catch (TimeoutException e) {
            // Sometimes, for no obvious reason, the browser/JS doesn't set document.readyState to 'complete' correctly
            // but that's no reason to let the test fail; after the timeout the page is surely fully loaded
            log.warn("waitForPageToLoad time exceeded!");
        }
    }

}
