const Router = require('@koa/router');
const jwt = require('jsonwebtoken');

const router = new Router();
const JWT_SECRET = process.env.JWT_SECRET || 'airops_jwt_secret_local';

// Demo login — in production integrate with Azure AD
router.post('/login', async (ctx) => {
  const { employeeId, password } = ctx.request.body;

  // TODO: Replace with real auth (Azure AD / OAuth2)
  const demoUsers = {
    'EMP001': { password: 'demo123', role: 'GROUND_HANDLER', name: 'James Carter' },
    'EMP004': { password: 'demo123', role: 'SUPERVISOR', name: 'Lisa Torres' },
    'ADMIN':  { password: 'admin123', role: 'ADMIN', name: 'System Admin' },
  };

  const user = demoUsers[employeeId];
  if (!user || user.password !== password) {
    ctx.status = 401;
    ctx.body = { error: 'Invalid credentials' };
    return;
  }

  const token = jwt.sign(
    { sub: employeeId, name: user.name, role: user.role },
    JWT_SECRET,
    { expiresIn: '8h' }
  );

  ctx.body = {
    token,
    employeeId,
    name: user.name,
    role: user.role,
    expiresIn: '8h',
  };
});

router.post('/refresh', async (ctx) => {
  const { token } = ctx.request.body;
  try {
    const decoded = jwt.verify(token, JWT_SECRET);
    const newToken = jwt.sign(
      { sub: decoded.sub, name: decoded.name, role: decoded.role },
      JWT_SECRET,
      { expiresIn: '8h' }
    );
    ctx.body = { token: newToken };
  } catch {
    ctx.status = 401;
    ctx.body = { error: 'Invalid or expired token' };
  }
});

module.exports = router;
