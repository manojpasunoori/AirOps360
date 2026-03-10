const Router = require('@koa/router');
const router = new Router();

router.get('/', async (ctx) => {
  ctx.body = {
    status: 'UP',
    service: 'koa-api-gateway',
    version: '1.0.0',
    timestamp: new Date().toISOString(),
    uptime: process.uptime(),
  };
});

module.exports = router;
