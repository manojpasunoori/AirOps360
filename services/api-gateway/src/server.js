const config = require("./config");
const { createApp } = require("./app");

const app = createApp();

if (require.main === module) {
  app.listen(config.port, () => {
    // eslint-disable-next-line no-console
    console.log(`AirOps360 API gateway listening on port ${config.port}`);
  });
}

module.exports = {
  app,
};
