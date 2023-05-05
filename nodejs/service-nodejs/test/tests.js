import config from './utils/config.js';
import test from 'tape';
import roi from 'roi';
import tokenRequester from 'keycloak-request-token';

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
  });
});