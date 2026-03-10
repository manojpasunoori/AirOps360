const Koa = require('koa');
const Router = require('@koa/router');
const cors = require('@koa/cors');
const bodyParser = require('koa-bodyparser');
const helmet = require('koa-helmet');
const compress = require('koa-compress');
const jwt = require('koa-jwt');
const ratelimit = require('koa-ratelimit');
const Redis = require('ioredis');
const pino = require('pino');
const { ApolloServer } = require('@apollo/server');
const { koaMiddleware } = require('@as-integrations/koa');

const inventoryRoutes = require('./routes/inventory');
const cargoRoutes = require('./routes/cargo');
const crewRoutes = require('./routes/crew');
const baggageRoutes = require('./routes/baggage');
const authRoutes = require('./routes/auth');
const healthRoutes = require('./routes/health');
const { typeDefs, resolvers } = require('./graphql/schema');
const { errorHandler } = require('./middleware/errorHandler');
const { requestLogger } = require('./middleware/requestLogger');
const { metricsMiddleware } = require('./middleware/metrics');

require('dotenv').config();

const logger = pino({ level: process.env.LOG_LEVEL || 'info' });
const app = new Koa();
const router = new Router();

// ── Redis for rate limiting ───────────────────────────────────
const redis = new Redis(process.env.REDIS_URL || 'redis://localhost:6379');

redis.on('connect', () => logger.info('Redis connected'));
redis.on('error', (err) => logger.error({ err }, 'Redis error'));

// ── Rate Limiting ─────────────────────────────────────────────
app.use(ratelimit({
  driver: 'redis',
  db: redis,
  duration: 60000,        // 1 minute window
  max: 200,               // 200 requests per minute
  id: (ctx) => ctx.ip,
  headers: {
    remaining: 'X-RateLimit-Remaining',
    reset: 'X-RateLimit-Reset',
    total: 'X-RateLimit-Limit',
  },
  whitelist: (ctx) => ctx.path === '/health',
}));

// ── Core Middleware ───────────────────────────────────────────
app.use(helmet());
app.use(cors({
  origin: process.env.ALLOWED_ORIGINS || '*',
  allowMethods: ['GET', 'POST', 'PUT', 'DELETE', 'PATCH'],
  allowHeaders: ['Authorization', 'Content-Type', 'X-Request-ID'],
}));
app.use(compress());
app.use(bodyParser());
app.use(errorHandler);
app.use(requestLogger(logger));
app.use(metricsMiddleware);

// ── Public Routes (no auth) ───────────────────────────────────
router.use('/health', healthRoutes.routes());
router.use('/auth', authRoutes.routes());

// ── Protected Routes (JWT required) ──────────────────────────
app.use(jwt({
  secret: process.env.JWT_SECRET || 'airops_jwt_secret_local',
  passthrough: false,
}).unless({
  path: [/^\/health/, /^\/auth/, /^\/graphql/]
}));

router.use('/api/v1/inventory', inventoryRoutes.routes());
router.use('/api/v1/cargo',     cargoRoutes.routes());
router.use('/api/v1/crew',      crewRoutes.routes());
router.use('/api/v1/baggage',   baggageRoutes.routes());

app.use(router.routes());
app.use(router.allowedMethods());

// ── GraphQL ───────────────────────────────────────────────────
async function startApollo() {
  const apolloServer = new ApolloServer({ typeDefs, resolvers });
  await apolloServer.start();

  const graphqlRouter = new Router();
  graphqlRouter.post('/graphql', koaMiddleware(apolloServer, {
    context: async ({ ctx }) => ({ user: ctx.state.user }),
  }));
  app.use(graphqlRouter.routes());
}

// ── Start ─────────────────────────────────────────────────────
const PORT = process.env.PORT || 3000;

async function start() {
  await startApollo();
  app.listen(PORT, () => {
    logger.info(`AirOps360 API Gateway running on port ${PORT}`);
    logger.info(`GraphQL endpoint: http://localhost:${PORT}/graphql`);
  });
}

start().catch((err) => {
  logger.error({ err }, 'Failed to start gateway');
  process.exit(1);
});

module.exports = app;
