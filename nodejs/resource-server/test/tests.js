import tokenRequester from 'keycloak-request-token';
import assert from 'node:assert';
import test from 'node:test';
import config from '../config/config.js';

test('accesses the \'public\' route without credentials.', async () => {
  const response = await fetch('http://localhost:3000/service/public');
  const data = await response.json();

  assert.strictEqual(data.message, 'public');
});

test('denies the \'secured\' route without credentials.', async () => {
  const response = await fetch('http://localhost:3000/service/secured');

  assert.strictEqual(response.statusText, 'Forbidden');
});

test('denies the \'admin\' route without credentials.', async () => {
  const response = await fetch('http://localhost:3000/service/admin');

  assert.strictEqual(response.statusText, 'Forbidden');
});

test('accesses the \'secured\' route with credentials.', async () => {
  const headers = { authorization: `Bearer ${await tokenRequester(config.baseUrl, config.token)}` };
  const response = await fetch('http://localhost:3000/service/secured', { headers });
  const data = await response.json();

  assert.strictEqual(data.message, 'secured');
});

test('accesses the \'admin\' route with credentials.', async () => {
  config.token.username = 'test-admin';

  const headers = { authorization: `Bearer ${await tokenRequester(config.baseUrl, config.token)}` };
  const response = await fetch('http://localhost:3000/service/admin', { headers });
  const data = await response.json();

  assert.strictEqual(data.message, 'admin');
});