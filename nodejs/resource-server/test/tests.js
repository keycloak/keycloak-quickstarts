import tokenRequester from 'keycloak-request-token';
import test from 'tape';
import config from './utils/config.js';

test('Should test public route with no credentials.', async (t) => {
  const response = await fetch('http://localhost:3000/service/public');
  const data = await response.json();

  t.equal(data.message, 'public');
});

test('Should test secured route with no credentials.', async (t) => {
  const response = await fetch('http://localhost:3000/service/secured');

  t.equal(response.statusText, 'Forbidden');
});

test('Should test admin route with no credentials.', async (t) => {
  const response = await fetch('http://localhost:3000/service/admin');

  t.equal(response.statusText, 'Forbidden');
});

test('Should test secured route with user credentials.', async (t) => {
  const headers = { authorization: `Bearer ${await tokenRequester(config.baseUrl, config.token)}` };
  const response = await fetch('http://localhost:3000/service/secured', { headers });
  const data = await response.json();

  t.equal(data.message, 'secured');
});

test('Should test secured route with admin credentials.', async (t) => {
  config.token.username = 'test-admin';

  const headers = { authorization: `Bearer ${await tokenRequester(config.baseUrl, config.token)}` };
  const response = await fetch('http://localhost:3000/service/admin', { headers });
  const data = await response.json();

  t.equal(data.message, 'admin');
});