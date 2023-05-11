import config from './utils/config.js';
import test from 'tape';
import roi from 'roi';
import tokenRequester from 'keycloak-request-token';

test('Should test public route with no credentials.', async (t) => {
  const options = {
    'endpoint': 'http://localhost:3000/service/public'
  };

  let response = await roi.get(options);
  t.equal(JSON.parse(response.body).message, 'public');
});

test('Should test secured route with no credentials.', async (t) => {
  const options = {
    'endpoint': 'http://localhost:3000/service/secured'
  };

  try {
    await roi.get(options);
    t.fail('Should never reach this block');
  } catch (error) {
    t.equal(error.toString(), 'Access denied');
  }
});

test('Should test admin route with no credentials.', async (t) => {
  const options = {
    'endpoint': 'http://localhost:3000/service/admin'
  };

  try {
    await roi.get(options);
    t.fail('Should never reach this block');
  } catch (error) {
    t.equal(error.toString(), 'Access denied');
  }
});

test('Should test secured route with user credentials.', async (t) => {
  const options = {
    endpoint: 'http://localhost:3000/service/secured',
    headers: {
      Authorization: 'Bearer ' + await tokenRequester(config.baseUrl, config.token)
    }
  };

  let response = await roi.get(options);
  t.equal(JSON.parse(response.body).message, 'secured');
});

test('Should test secured route with admin credentials.', async (t) => {
  config.token.username = 'test-admin';
  const options = {
    endpoint: 'http://localhost:3000/service/admin',
    headers: {
      Authorization: 'Bearer ' + await tokenRequester(config.baseUrl, config.token)
    }
  };

  let response = await roi.get(options);
  t.equal(JSON.parse(response.body).message, 'admin');
});