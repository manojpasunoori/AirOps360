# api-gateway

Koa-based gateway for AirOps360 APIs.

## Commit 13 scope

This service now includes a minimal Koa gateway skeleton with:
- package manifest and runtime dependencies
- environment-driven upstream service configuration
- health endpoint at `/health`
- service map endpoint at `/api/services`
- centralized Koa app bootstrap with basic 404 handling

## Planned responsibilities
- Route client traffic to backend services
- Aggregate service responses
- Provide a unified API surface for the dashboard
