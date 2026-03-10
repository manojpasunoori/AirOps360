const Router = require('@koa/router');
const axios = require('axios');

const router = new Router();
const WMS_URL = process.env.WMS_SERVICE_URL || 'http://localhost:8081';

// Proxy helper
async function proxyRequest(ctx, method, path, body = null) {
  try {
    const response = await axios({
      method,
      url: `${WMS_URL}${path}`,
      data: body,
      headers: {
        'Content-Type': 'application/json',
        'X-User-Id': ctx.state.user?.sub || 'anonymous',
      },
      timeout: 10000,
    });
    ctx.status = response.status;
    ctx.body = response.data;
  } catch (err) {
    ctx.status = err.response?.status || 503;
    ctx.body = { error: err.response?.data?.message || 'WMS service unavailable' };
  }
}

router.post('/receive',           async (ctx) => proxyRequest(ctx, 'POST', '/api/v1/inventory/receive', ctx.request.body));
router.post('/pick',              async (ctx) => proxyRequest(ctx, 'POST', '/api/v1/inventory/pick', ctx.request.body));
router.get('/item/:sku',          async (ctx) => proxyRequest(ctx, 'GET', `/api/v1/inventory/item/${ctx.params.sku}`));
router.get('/items',              async (ctx) => proxyRequest(ctx, 'GET', '/api/v1/inventory/items'));
router.get('/summary',            async (ctx) => proxyRequest(ctx, 'GET', '/api/v1/inventory/summary'));
router.get('/low-stock',          async (ctx) => proxyRequest(ctx, 'GET', `/api/v1/inventory/low-stock?threshold=${ctx.query.threshold || 10}`));
router.get('/locations/available', async (ctx) => proxyRequest(ctx, 'GET', '/api/v1/inventory/locations/available'));

module.exports = router;
