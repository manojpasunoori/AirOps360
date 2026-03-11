const Koa = require("koa");
const bodyParser = require("koa-bodyparser");
const client = require("prom-client");
const healthRoutes = require("./routes/health");
const serviceRoutes = require("./routes/services");

function createApp() {
  const app = new Koa();
  const register = new client.Registry();

  client.collectDefaultMetrics({ register });

  app.use(bodyParser());
  app.use(async (ctx, next) => {
    if (ctx.path === "/metrics") {
      ctx.set("Content-Type", register.contentType);
      ctx.body = await register.metrics();
      return;
    }

    await next();
  });
  app.use(healthRoutes.routes());
  app.use(healthRoutes.allowedMethods());
  app.use(serviceRoutes.routes());
  app.use(serviceRoutes.allowedMethods());

  app.use(async (ctx) => {
    if (ctx.status === 404) {
      ctx.body = {
        error: "not_found",
        message: `No route configured for ${ctx.method} ${ctx.path}`,
      };
    }
  });

  return app;
}

module.exports = {
  createApp,
};
