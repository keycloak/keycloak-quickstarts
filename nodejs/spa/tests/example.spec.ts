import { test, expect } from '@playwright/test';

test('Login', async ({ page }) => {
  await page.goto('http://localhost:8080/');

  await login(page, 'alice', 'password')

  await expect(await page.locator('id=name')).toHaveText('Hello Alice Liddel');
});

test('Logout', async ({ page }) => {
  await page.goto('http://localhost:8080/');

  await login(page, 'alice', 'password')

  await expect(await page.locator('id=name')).toHaveText('Hello Alice Liddel');
  await page.locator('id=logout').click();

  await expect(page).toHaveTitle('Sign in to quickstart');
});

test('Show Access Token', async ({ page }) => {
  await page.goto('http://localhost:8080/');

  await login(page, 'alice', 'password')

  await expect(await page.locator('id=name')).toHaveText('Hello Alice Liddel');
  await page.locator('id=showAccessToken').click();

  await expect(await page.locator('id=output')).toContainText('"preferred_username": "alice"');
});

async function login(page, username, password) {
  await expect(page).toHaveTitle('Sign in to quickstart');
  await page.locator('id=username').fill('alice')
  await page.locator('id=password').fill('password')
  await page.locator('id=kc-login').click()
}
