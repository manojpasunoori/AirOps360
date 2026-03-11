const Router = require("@koa/router");

const router = new Router();

router.get("/health", async (ctx) => {
  ctx.body = {
    service: "api-gateway",
    status: "ok",
  };
});

module.exports = router;
