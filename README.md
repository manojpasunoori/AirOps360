# AirOps360

<p align="center">
  <strong>Event-driven airport ground and warehouse operations platform</strong><br/>
  Modeling flight arrival, baggage scanning, cargo unloading, warehouse receiving, inventory updates, worker coordination, and operational analytics.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Architecture-Event%20Driven-0F766E?style=for-the-badge" alt="Event Driven" />
  <img src="https://img.shields.io/badge/Backend-Java%20%7C%20.NET%20%7C%20Python%20%7C%20Node-1D4ED8?style=for-the-badge" alt="Polyglot Backend" />
  <img src="https://img.shields.io/badge/Frontend-React%20%2B%20TypeScript-EA580C?style=for-the-badge" alt="React TypeScript" />
  <img src="https://img.shields.io/badge/Platform-Docker%20%7C%20Kubernetes%20%7C%20ArgoCD-7C3AED?style=for-the-badge" alt="Platform" />
  <img src="https://img.shields.io/badge/Observability-Prometheus%20%7C%20Grafana-C2410C?style=for-the-badge" alt="Observability" />
</p>

## Why AirOps360 Matters

AirOps360 is designed to simulate the operational heartbeat of an airport ramp and warehouse ecosystem. Instead of treating flight telemetry, baggage scans, cargo unload activity, warehouse receiving, and inventory updates as isolated systems, it brings them together through an event backbone and a unified operations dashboard.

From a recruiter or engineering leadership perspective, this repo demonstrates:
- polyglot backend design across Spring Boot, .NET, FastAPI, and Node.js
- event-driven system thinking with Kafka topics and shared contracts
- operational platform maturity through Docker, Kubernetes, ArgoCD, Prometheus, Grafana, and GitHub Actions
- full-stack product thinking with a React dashboard layered over backend services and gateway APIs
- developer-experience work such as local environment setup, CI stabilization, BDD/E2E coverage, and architecture documentation

## Platform Snapshot

| Area | Current Direction |
| --- | --- |
| Domain | Airport ground operations + warehouse coordination |
| Architectural style | Event-driven microservices |
| Messaging backbone | Apache Kafka |
| Primary backend stacks | Java 17, .NET 8, Python FastAPI, Node.js Koa |
| Data layer | PostgreSQL, Redis, MongoDB |
| Frontend | React + TypeScript |
| Delivery platform | Docker, Kubernetes, ArgoCD, GitHub Actions |
| Observability | Prometheus + Grafana starter assets |
| Test surface | Java unit tests, Python pytest, NUnit + Moq, Playwright, Cucumber |

## Core Event Topics

- `flight.arrival`
- `baggage.scan`
- `cargo.unload`
- `warehouse.receive`
- `inventory.update`

## System Architecture

```mermaid
flowchart LR
    classDef source fill:#DBEAFE,stroke:#1D4ED8,color:#0F172A,stroke-width:1px;
    classDef service fill:#DCFCE7,stroke:#15803D,color:#0F172A,stroke-width:1px;
    classDef gateway fill:#FDE68A,stroke:#B45309,color:#0F172A,stroke-width:1px;
    classDef store fill:#FCE7F3,stroke:#BE185D,color:#0F172A,stroke-width:1px;
    classDef bus fill:#EDE9FE,stroke:#6D28D9,color:#0F172A,stroke-width:1px;
    classDef ui fill:#FED7AA,stroke:#C2410C,color:#0F172A,stroke-width:1px;

    OpenSky["OpenSky Network"] --> FlightService["flight-service<br/>Spring Boot"]
    FAA["FAA NAS Status"] --> FlightService
    Weather["OpenWeather"] --> FlightService
    Simulator["simulator-service<br/>FastAPI"] --> Kafka[("Kafka Topics")]

    FlightService --> Kafka
    CargoService["cargo-service<br/>FastAPI"] --> Kafka
    WarehouseService["warehouse-service<br/>Spring Boot"] --> Kafka
    BaggageService["baggage-service<br/>.NET Worker"] --> Kafka
    Kafka --> InventoryService["inventory-service<br/>Spring Boot"]
    Kafka --> AnalyticsService["analytics-service<br/>planned"]

    Dashboard["operations-dashboard<br/>React + TypeScript"] --> ApiGateway["api-gateway<br/>Node Koa"]
    ApiGateway --> FlightService
    ApiGateway --> CargoService
    ApiGateway --> WarehouseService
    ApiGateway --> InventoryService

    FlightService --> Postgres[("PostgreSQL")]
    WarehouseService --> Postgres
    InventoryService --> Redis[("Redis")]
    CargoService --> Mongo[("MongoDB")]

    class OpenSky,FAA,Weather source;
    class FlightService,CargoService,WarehouseService,BaggageService,InventoryService,AnalyticsService,Simulator service;
    class ApiGateway gateway;
    class Postgres,Redis,Mongo store;
    class Kafka bus;
    class Dashboard ui;
```

## Operational Event Flow

```mermaid
sequenceDiagram
    autonumber
    participant Telemetry as OpenSky / Simulator
    participant Flight as flight-service
    participant Kafka as Kafka
    participant Baggage as baggage-service
    participant Cargo as cargo-service
    participant Warehouse as warehouse-service
    participant Inventory as inventory-service
    participant ApiGateway as api-gateway
    participant Dashboard as operations-dashboard

    Telemetry->>Flight: Flight telemetry and arrival signal
    Flight->>Kafka: Publish flight.arrival
    Telemetry->>Kafka: Publish baggage.scan, cargo.unload, warehouse.receive
    Kafka->>Baggage: Consume baggage.scan
    Kafka->>Cargo: Consume cargo.unload
    Kafka->>Warehouse: Consume warehouse.receive
    Warehouse->>Kafka: Publish inventory.update
    Kafka->>Inventory: Consume inventory.update
    Dashboard->>ApiGateway: Request operations data
    ApiGateway->>Flight: Get telemetry and status
    ApiGateway->>Inventory: Get cache and inventory state
    ApiGateway-->>Dashboard: Unified operational response
```

## Service Landscape

| Service | Stack | Role |
| --- | --- | --- |
| `flight-service` | Spring Boot | Fetches and normalizes flight telemetry from public sources |
| `baggage-service` | .NET Worker | Processes baggage scan events and worker-cycle logic |
| `cargo-service` | FastAPI | Models cargo unload intake workflows |
| `warehouse-service` | Spring Boot | Handles warehouse receiving workflows |
| `inventory-service` | Spring Boot | Maintains Redis-backed inventory cache behavior |
| `simulator-service` | FastAPI | Generates synthetic baggage, cargo, and warehouse events |
| `api-gateway` | Node.js Koa | Exposes a unified API surface for the dashboard |
| `operations-dashboard` | React + TypeScript | Presents flight, baggage, warehouse, cargo, and alert views |
| `worker-service` | Planned | Worker assignment and task orchestration |
| `analytics-service` | Planned | KPI aggregation and cross-service operational analytics |

## What Is Implemented Today

The repository currently contains:
- local Docker infrastructure for PostgreSQL, Redis, MongoDB, Kafka, and Kafka UI
- Kafka topic bootstrapping and shared JSON event schemas
- Spring Boot service skeletons for flight, warehouse, and inventory domains
- FastAPI skeletons for cargo and simulator domains
- a .NET baggage worker with NUnit + Moq test coverage
- a Koa API gateway with metrics and BDD coverage
- a React operations dashboard shell with flight, warehouse, and baggage-focused UI views
- Kubernetes base manifests, a development overlay, and ArgoCD application definitions
- Prometheus scrape configuration and Grafana starter dashboards
- GitHub Actions CI covering Java, Python, .NET, Node, Playwright, and Cucumber

## Engineering Highlights

- **Polyglot design:** the platform deliberately mixes the right runtime for the right job instead of forcing one language across every service.
- **Event-first modeling:** Kafka topics and schema docs shape the workflow boundaries before deeper persistence or orchestration logic.
- **Operational maturity:** the repo includes containerization, Kubernetes manifests, ArgoCD GitOps setup, metrics exposure, dashboards, and CI coverage.
- **Quality follow-through:** several post-build fixes hardened CI, test packaging, metrics routing, and documentation rendering until the pipeline was green.
- **Portfolio depth:** this is not just a UI demo or an API sample; it shows systems thinking across ingestion, processing, infrastructure, observability, and developer experience.

## Repository Layout

```text
AirOps360/
|-- services/
|   |-- flight-service/
|   |-- baggage-service/
|   |-- cargo-service/
|   |-- warehouse-service/
|   |-- inventory-service/
|   |-- worker-service/
|   |-- simulator-service/
|   |-- analytics-service/
|   `-- api-gateway/
|-- frontend/
|   `-- operations-dashboard/
|-- infrastructure/
|   |-- docker/
|   |-- kubernetes/
|   `-- argocd/
|-- tests/
`-- docs/
```

## Local Development And Testing

### Start shared infrastructure
```bash
cp .env.example .env
docker compose up -d
```

### Run core test surfaces
```bash
cd services/flight-service && mvn test
cd services/warehouse-service && mvn test
cd services/inventory-service && mvn test
cd services/cargo-service && python -m pip install -r requirements.txt && python -m pytest tests -q
cd services/simulator-service && python -m pip install -r requirements.txt && python -m pytest tests -q
cd services/baggage-service && dotnet test tests/BaggageService.Tests.csproj --configuration Release
cd services/api-gateway && npm install && node --check src/server.js
cd frontend/operations-dashboard && npm install && npm run build
```

### Run browser and BDD suites
```bash
cd frontend/operations-dashboard && npm install && npm run build
cd tests/e2e-playwright && npm install && npx playwright install --with-deps chromium && npm test
cd services/api-gateway && npm install
cd tests/bdd-cucumber && npm install && npm test
```

## Documentation Map

- `docs/architecture.md` for the detailed system context and event-flow diagrams
- `docs/local-development.md` for local setup and testing commands
- `docs/observability.md` for Prometheus and Grafana assets
- `docs/automated-tests.md` for CI and test coverage
- `docs/deployment/kubernetes-deployment.md` for Kubernetes deployment guidance
- `docs/repository-structure.md` for canonical repo organization

## Data Sources

AirOps360 is designed around free public or synthetic sources:
- OpenSky Network for flight telemetry
- FAA NAS Status for delay and disruption context
- OpenWeather for weather-driven operational impact
- simulator-service for synthetic baggage, cargo, and warehouse events

## Current Notes

- The repo still contains a few older exploratory folders outside the canonical AirOps360 layout.
- `worker-service` and `analytics-service` are documented as planned services and not yet implemented at the same depth as the active services.
- The strongest source of truth for the current platform shape is the combination of `services/`, `frontend/operations-dashboard/`, `infrastructure/`, and the documentation under `docs/`.
