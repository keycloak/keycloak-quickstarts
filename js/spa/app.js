import express from 'express';

const app = express();
const port = 8080;

app.use('/', express.static('public'));

app.use('/vendor/keycloak-js', express.static('node_modules/keycloak-js/dist'));
app.use('/vendor/jwt-decode', express.static('node_modules/jwt-decode/build/esm'));
app.use('/vendor/@noble/hashes', express.static('node_modules/@noble/hashes/esm'));

app.listen(port, () => {
  console.log(`Listening on port ${port}.`);
});
