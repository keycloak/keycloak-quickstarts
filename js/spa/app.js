import express from 'express';
import stringReplace from 'string-replace-middleware';

const app = express();
const port = 8080;

const options = {
  contentTypeFilterRegexp: /.*/,
}

app.use(stringReplace({
  "${KC_URL}": process.env.KC_URL || "http://localhost:8180"
}, options));

app.use('/', express.static('public'));

app.listen(port, () => {
  console.log(`Listening on port ${port}.`);
});
