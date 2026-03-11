const Koa = require("koa");
const bodyParser = require("koa-bodyparser");
const config = require("./config");
const healthRoutes = require("./routes/health");
const serviceRoutes = require("./routes/services");

const app = new Koa();

app.use(bodyParser());
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
