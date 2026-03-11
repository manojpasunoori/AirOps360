const client = require("prom-client");
const config = require("./config");
const { createApp } = require("./app");

const app = createApp();
const register = new client.Registry();

client.collectDefaultMetrics({ register });
app.use(async (ctx, next) => {
  if (ctx.path === "/metrics") {
    ctx.set("Content-Type", register.contentType);
    ctx.body = await register.metrics();
    return;
  }

  await next();
});

if (require.main === module) {
  app.listen(config.port, () => {
    // eslint-disable-next-line no-console
    console.log(`AirOps360 API gateway listening on port ${config.port}`);
  });
}

module.exports = {
  app,
};
