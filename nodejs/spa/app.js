import express from 'express';
import stringReplace from 'string-replace-middleware';

const app = express();
const port = 8080;
var KC_URL = process.env.KC_URL || "http://localhost:8180/auth";

app.use(stringReplace({
  'KC_URL': KC_URL
}));

app.use('/', express.static('src/main/webapp/'));

app.listen(port, () => {
  console.log(`Single-Page Application listening on port ${port}`);
});
