import express from 'express';

const app = express();
const port = 8080;

app.use('/app-html5', express.static('src/main/webapp/'));

app.listen(port, () => {
  console.log(`app-nodejs-html5 listening on port ${port}`);
});
