/*
 * Copyright 2024 Red Hat, Inc. and/or its affiliates
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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ExtendedAdminPage {

    @FindBy(
            id = "nav-item-page-section/Todo"
    )
    private WebElement todoMenuItem;

    @FindBy(
            xpath = "//button[@data-testid='there-are-no-items-empty-action']"
    )
    private WebElement addButton;

    @FindBy(
            xpath = "//input[@data-testid='name']"
    )
    private WebElement nameInput;

    @FindBy(
            xpath = "//textarea[@data-testid='description']"
    )
    private WebElement descriptionInput;

    @FindBy(
            xpath = "//button[@data-testid='save']"
    )
    private WebElement saveButton;

    @FindBy(
            css = ".pf-m-success"
    )
    private WebElement alert;

    @Drone
    private WebDriver webDriver;

    public static final String ADMIN_CONSOLE = "/admin/master/console/";

    public void clickTodoMenuItem() {
        this.todoMenuItem.click();
    }

    public void navigateTo() {
        webDriver.navigate().to(ExtendAdminConsoleTest.KEYCLOAK_URL + ADMIN_CONSOLE);
    }

    public boolean isTodoMenuPresent() {
        return this.todoMenuItem.isEnabled();
    }

    public boolean isOverviewPage() {
        return new WebDriverWait(webDriver, 5).until(ExpectedConditions.textToBe(By.id("view-header-subkey"), "Here you can store your Todo items"));
    }

    public void clickAddButton() {
        addButton.click();
    }

    public void fillTodoForm(String name, String description) {
        nameInput.sendKeys(name);
        descriptionInput.sendKeys(description);
    }

    public void clickSave() {
        saveButton.click();
    }

    public boolean isSaved() {
        return alert.isEnabled();
    }
}
