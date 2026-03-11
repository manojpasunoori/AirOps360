# Automated Tests

AirOps360 now includes a starter automated test surface across the current stacks.

## Test suites
- `services/baggage-service/tests/` contains NUnit tests for the baggage worker and uses Moq for logger verification
- `tests/bdd-cucumber/` contains a Cucumber scenario for API gateway service discovery
- `tests/e2e-playwright/` contains a Playwright browser test for the operations dashboard shell

## Current coverage
- Baggage processor output and worker logging behavior
- API gateway `/api/services` response contract
- Dashboard rendering of the flight, warehouse, baggage, cargo, and alert sections

## CI integration
The GitHub Actions workflow now runs:
- Java service tests
- Python service tests
- .NET baggage worker tests
- dashboard build verification
- Playwright browser tests
- Cucumber BDD tests

## Notes
- The placeholder `tests/bdd-cucumber/{features,steps}` folder from earlier scaffolding remains on disk but is not used by the active test suite.
- These tests target the current scaffolded behavior and are intended as a base for deeper scenario coverage later.
