import { test, expect } from '@playwright/test';

test('Login', async ({ page }) => {
  await page.goto('http://localhost:8080/');

  await login(page)

  await expect(await page.locator('id=name')).toHaveText('Hello Alice Liddel');
});

test('Logout', async ({ page }) => {
  await page.goto('http://localhost:8080/');

  await login(page)
  await logout(page);
});

test('Show Access Token', async ({ page }) => {
  await page.goto('http://localhost:8080/');

  await login(page)

  await page.locator('id=showAccessToken').click();
  await expect(await page.locator('id=output')).toContainText('"preferred_username": "alice"');
});

test('Show My Account', async ({ page }) => {
  await page.goto('http://localhost:8080/');

  await login(page)

  await page.locator('id=showMyAccount').click();
  await expect(page).toHaveTitle('Keycloak Account Management');
});

async function login(page) {
  await expect(page).toHaveTitle('Sign in to quickstart');
  await page.locator('id=username').fill('alice')
  await page.locator('id=password').fill('alice')
  await page.locator('id=kc-login').click()
}

async function logout(page) {
  await page.locator('id=logout').click();
  await expect(page).toHaveTitle('Sign in to quickstart');
}
