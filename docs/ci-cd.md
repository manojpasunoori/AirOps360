# CI/CD

AirOps360 uses GitHub Actions for continuous integration on the current application surface.

## Current workflow
The workflow at `.github/workflows/ci-cd.yml` runs on pushes to `main` and on pull requests targeting `main`.

It currently covers:
- Java 17 Maven test runs for `flight-service`, `warehouse-service`, and `inventory-service`
- Python 3.12 dependency install and `pytest` runs for `cargo-service` and `simulator-service`
- .NET 8 restore and build for `baggage-service`
- Node.js 20 install plus syntax/build verification for `api-gateway` and `frontend/operations-dashboard`

## Notes
- The Koa gateway currently has no dedicated test script, so the workflow performs a Node syntax check on `src/server.js`.
- The dashboard currently verifies with `npm run build`.
- Docker image publishing and ArgoCD deployment are intentionally deferred to later commits in the implementation plan.
