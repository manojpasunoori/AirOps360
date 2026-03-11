# Local Development

This guide explains how to run and test the current AirOps360 repository locally.

## Prerequisites
- Docker Desktop with Compose
- Java 17 and Maven
- Python 3.12
- Node.js 20+
- .NET 8 SDK

## 1. Start shared infrastructure
Copy the example environment file if you want custom ports or credentials:

```bash
cp .env.example .env
```

Bring up the local backing services:

```bash
docker compose up -d
```

This starts:
- PostgreSQL
- Redis
- MongoDB
- Kafka
- Kafka UI
- Kafka topic bootstrap job

## 2. Run service checks locally

### Java services
```bash
cd services/flight-service && mvn test
cd services/warehouse-service && mvn test
cd services/inventory-service && mvn test
```

### Python services
```bash
cd services/cargo-service && python -m pip install -r requirements.txt && python -m pytest tests -q
cd services/simulator-service && python -m pip install -r requirements.txt && python -m pytest tests -q
```

### .NET baggage worker
```bash
cd services/baggage-service && dotnet test tests/BaggageService.Tests.csproj --configuration Release
```

### Node services
```bash
cd services/api-gateway && npm install && node --check src/server.js
cd frontend/operations-dashboard && npm install && npm run build
```

## 3. Run browser and BDD tests

### Playwright dashboard test
```bash
cd frontend/operations-dashboard && npm install && npm run build
cd tests/e2e-playwright && npm install && npx playwright install --with-deps chromium && npm test
```

### Cucumber gateway test
```bash
cd services/api-gateway && npm install
cd tests/bdd-cucumber && npm install && npm test
```

## 4. Inspect local platform endpoints
After infrastructure is running, these local ports are expected:
- PostgreSQL: `localhost:5432`
- Redis: `localhost:6379`
- MongoDB: `localhost:27017`
- Kafka broker: `localhost:9092`
- Kafka UI: `http://localhost:8080`

## 5. Read the platform docs
- `docs/architecture.md` for the system overview and event flow
- `docs/observability.md` for Prometheus and Grafana assets
- `docs/automated-tests.md` for current CI and test coverage
- `docs/deployment/kubernetes-deployment.md` for Kubernetes overlay deployment

## Local Enhancement Suggestions
- Add lockfiles for Node workspaces so npm installs are reproducible locally and in CI.
- Add service run scripts or a root developer task runner for the non-containerized services.
- Expand the API gateway tests beyond service discovery into request proxy and error handling behavior.
