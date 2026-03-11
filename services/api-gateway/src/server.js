const Koa = require("koa");
const Router = require("@koa/router");
const bodyParser = require("koa-bodyparser");
const client = require("prom-client");
const config = require("./config");
const healthRoutes = require("./routes/health");
const serviceRoutes = require("./routes/services");

const app = new Koa();
const metricsRouter = new Router();
const register = new client.Registry();

client.collectDefaultMetrics({ register });

metricsRouter.get("/metrics", async (ctx) => {
  ctx.set("Content-Type", register.contentType);
  ctx.body = await register.metrics();
});

app.use(bodyParser());
app.use(metricsRouter.routes());
app.use(metricsRouter.allowedMethods());
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

app.listen(config.port, () => {
  // eslint-disable-next-line no-console
  console.log(`AirOps360 API gateway listening on port ${config.port}`);
});
