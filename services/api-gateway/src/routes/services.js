const Router = require("@koa/router");
const config = require("../config");

const router = new Router({ prefix: "/api" });

router.get("/services", async (ctx) => {
  ctx.body = {
    gateway: "api-gateway",
    services: config.services,
  };
});

module.exports = router;
