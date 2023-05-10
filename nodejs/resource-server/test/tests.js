import config from './utils/config.js';
import test from 'tape';
import roi from 'roi';
import tokenRequester from 'keycloak-request-token';

test('Should test public route with no credentials.', async function (t) {
  const options = {
    'endpoint': 'http://localhost:3000/service/public'
  };

  let response = await roi.get(options);
  t.equal(JSON.parse(response.body).message, 'public');
  t.end();
});

test('Should test secured route with no credentials.', t => {
  const options = {
    'endpoint': 'http://localhost:3000/service/secured'
  };

  roi.get(options)
    .then(x => {
      t.fail('Should never reach this block');
    })
    .catch(e => {
      t.equal(e.toString(), 'Access denied');
      t.end();
    });
});

test('Should test admin route with no credentials.', t => {
  const options = {
    'endpoint': 'http://localhost:3000/service/admin'
  };

  roi.get(options)
    .then(x => {
      t.fail('Should never reach this block');
    })
    .catch(e => {
      t.equal(e.toString(), 'Access denied');
      t.end();
    });
});

test('Should test secured route with user credentials.', async function (t) {
  const options = {
    endpoint: 'http://localhost:3000/service/secured',
    headers: {
      Authorization: 'Bearer ' + await tokenRequester(config.baseUrl, config.token)
    }
  };

  let response = await roi.get(options);
  t.equal(JSON.parse(response.body).message, 'secured');
  t.end();
});

test('Should test secured route with admin credentials.', async function (t) {
  config.token.username = 'test-admin';
  const options = {
    endpoint: 'http://localhost:3000/service/admin',
    headers: {
      Authorization: 'Bearer ' + await tokenRequester(config.baseUrl, config.token)
    }
  };

  let response = await roi.get(options);
  t.equal(JSON.parse(response.body).message, 'admin');
  t.end();
});