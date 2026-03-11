# AirOps360

AirOps360 is an event-driven airport ground and warehouse operations platform. It models the operational flow from flight arrival through baggage scanning, cargo unloading, warehouse receiving, inventory updates, worker assignment, and analytics generation.

This repository is being built in small commits. Commit 1 establishes the canonical repository structure and lightweight service placeholders for the planned polyglot microservices platform.

## Planned architecture

- Event backbone: Apache Kafka
- Backend services: Spring Boot, .NET 8 workers, FastAPI, Node.js
- Data stores: PostgreSQL, MongoDB, Redis
- Frontend: React and TypeScript
- Platform: Docker, Kubernetes, ArgoCD, GitHub Actions
- Observability: OpenTelemetry, Prometheus, Grafana

## Core event topics

- `flight.arrival`
- `baggage.scan`
- `cargo.unload`
- `warehouse.receive`
- `inventory.update`

## Canonical repository layout

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
`-- docs/
```

## Planned services

| Service | Stack | Responsibility |
| --- | --- | --- |
| `flight-service` | Java 17 + Spring Boot | Flight telemetry ingestion and arrival events |
| `baggage-service` | C# .NET 8 Worker | Baggage scan event processing |
| `cargo-service` | Python FastAPI | Cargo unload workflows and cargo metadata |
| `warehouse-service` | Java 17 + Spring Boot | Warehouse receiving workflows |
| `inventory-service` | Service TBD | Inventory state and cache synchronization |
| `worker-service` | Node.js | Worker assignment and task orchestration |
| `simulator-service` | Service TBD | Synthetic airport operations event generation |
| `analytics-service` | Service TBD | KPI aggregation, alerts, and operational analytics |
| `api-gateway` | Node.js Koa | Unified API surface for the dashboard |

## Project notes

- Free public data sources planned: OpenSky Network, FAA NAS Status, OpenWeather, and a synthetic simulator service.
- The current repository also contains older exploratory folders. The paths listed above are the canonical AirOps360 structure for this implementation plan.
- Implementation proceeds in 25 commits, starting with repository scaffolding.

## Documentation

- Repository structure: `docs/repository-structure.md`
- Service-level placeholders: `services/*/README.md`
- Frontend placeholder: `frontend/operations-dashboard/README.md`
- Infrastructure placeholders: `infrastructure/*/README.md`
