'use strict';

const config = require('./config');
const test = require('tape');
const roi = require('roi');
const registration = require('keycloak-client-registration');
const tokenRequester = require('keycloak-request-token');

test('Should test public route with no credentials.', t => {
  const options = {
    'endpoint': 'http://localhost:3000/service/public'
  };

  roi.get(options)
    .then(x => {
      t.equal(JSON.parse(x.body).message, 'public');
      t.end();
    })
    .catch(e => {
      console.error(e);
      t.fail();
    });
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

registration.create(config.registration, config.testClient).then((v) => {
  config.registration.accessToken = v.registrationAccessToken;

  test('Should test secured route with user credentials.', t => {
    tokenRequester(config.baseUrl, config.token).then((token) => {
      const opt = {
        endpoint: 'http://localhost:3000/service/secured',
        headers: {
          Authorization: 'Bearer ' + token
        }
      };
      roi.get(opt)
        .then(x => {
          t.equal(JSON.parse(x.body).message, 'secured');
          t.end();
        })
        .catch(e => t.fail(e));

    }).catch((err) => {
      console.log('err', err);
    });
  });

  test('Should test secured route with admin credentials.', t => {
    config.token.username = 'test-admin';
    tokenRequester(config.baseUrl, config.token).then((token) => {
      const opt = {
        endpoint: 'http://localhost:3000/service/admin',
        headers: {
          Authorization: 'Bearer ' + token
        }
      };
      roi.get(opt)
        .then(x => {
          t.equal(JSON.parse(x.body).message, 'admin');
          t.end();
        })
        .catch(e => t.fail(e));

    }).catch((err) => {
      console.log('err', err);
    }).then(() => {
      registration.remove(config.registration, v.clientId).then((o) => {
        t.equal(o.statusCode, 204);
        t.equal(o.statusMessage, 'No Content');
      }).catch((e) => {
        console.error('Error removing client', e);
      });
    });
  });
}).catch((e) => {
  console.error('Error creating client. Please, check if you have accessToken properly configured', e);
})
