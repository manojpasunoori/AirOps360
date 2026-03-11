# CI/CD

AirOps360 uses GitHub Actions for continuous integration on the current application surface.

## Current workflow
The workflow at `.github/workflows/ci-cd.yml` runs on pushes to `main` and on pull requests targeting `main`.

It currently covers:
- Java 17 Maven test runs for `flight-service`, `warehouse-service`, and `inventory-service`
- Python 3.12 dependency install and `pytest` runs for `cargo-service` and `simulator-service`
- .NET 8 restore, build, and NUnit test runs for `baggage-service`
- Node.js 20 install plus syntax/build verification for `api-gateway` and `frontend/operations-dashboard`
- Playwright browser tests for the dashboard shell
- Cucumber BDD tests for the API gateway service map

## Notes
- The Koa gateway currently has no dedicated unit test script, so the workflow performs a Node syntax check on `src/server.js` and exercises behavior through the Cucumber suite.
- The dashboard verifies with `npm run build` and a Playwright browser test.
- Docker image publishing and ArgoCD deployment are intentionally deferred to later infrastructure expansions beyond this 25-commit implementation plan.
