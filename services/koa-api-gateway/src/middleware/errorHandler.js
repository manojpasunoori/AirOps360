const errorHandler = async (ctx, next) => {
  try {
    await next();
  } catch (err) {
    const status = err.status || err.statusCode || 500;
    const message = err.message || 'Internal Server Error';

    ctx.status = status;
    ctx.body = {
      error: {
        status,
        message,
        path: ctx.path,
        timestamp: new Date().toISOString(),
      }
    };

    // Don't log 4xx as errors
    if (status >= 500) {
      ctx.app.emit('error', err, ctx);
    }
  }
};

module.exports = { errorHandler };
