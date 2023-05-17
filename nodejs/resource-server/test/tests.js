import tokenRequester from 'keycloak-request-token';
import assert from 'node:assert';
import test from 'node:test';
import config from '../config/config.js';

const baseUrl = 'http://127.0.0.1:3000';

test('accesses the \'public\' route without credentials.', async () => {
  const response = await fetch(`${baseUrl}/public`);
  const data = await response.json();

  assert.strictEqual(data.message, 'public');
});

test('denies the \'secured\' route without credentials.', async () => {
  const response = await fetch(`${baseUrl}/secured`);

  assert.strictEqual(response.statusText, 'Forbidden');
});

test('denies the \'admin\' route without credentials.', async () => {
  const response = await fetch(`${baseUrl}/admin`);

  assert.strictEqual(response.statusText, 'Forbidden');
});

test('accesses the \'secured\' route with credentials.', async () => {
  const headers = { authorization: `Bearer ${await tokenRequester(config.baseUrl, config.token)}` };
  const response = await fetch(`${baseUrl}/secured`, { headers });
  const data = await response.json();

  assert.strictEqual(data.message, 'secured');
});

test('accesses the \'admin\' route with credentials.', async () => {
  config.token.username = 'admin';
  config.token.password = 'admin';

  const headers = { authorization: `Bearer ${await tokenRequester(config.baseUrl, config.token)}` };
  const response = await fetch(`${baseUrl}/admin`, { headers });
  const data = await response.json();

  assert.strictEqual(data.message, 'admin');
});