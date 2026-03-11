# Architecture

AirOps360 is organized as an event-driven airport ground and warehouse operations platform. The current repository contains service skeletons, shared event contracts, local infrastructure, Kubernetes deployment assets, and a dashboard shell that represent the target runtime shape.

## System Context

```mermaid
flowchart LR
    OpenSky[OpenSky Network] --> FlightService[flight-service\nSpring Boot]
    FAA[FAA NAS Status] --> FlightService
    Weather[OpenWeather] --> FlightService
    Simulator[simulator-service\nFastAPI] --> Kafka[(Kafka Topics)]
    FlightService --> Kafka
    CargoService[cargo-service\nFastAPI] --> Kafka
    WarehouseService[warehouse-service\nSpring Boot] --> Kafka
    BaggageService[baggage-service\n.NET Worker] --> Kafka
    Kafka --> InventoryService[inventory-service\nSpring Boot]
    Kafka --> AnalyticsService[analytics-service\nplanned]
    ApiGateway[api-gateway\nNode Koa] --> FlightService
    ApiGateway --> CargoService
    ApiGateway --> WarehouseService
    ApiGateway --> InventoryService
    Dashboard[operations-dashboard\nReact + TypeScript] --> ApiGateway
    FlightService --> Postgres[(PostgreSQL)]
    WarehouseService --> Postgres
    InventoryService --> Redis[(Redis)]
    CargoService --> Mongo[(MongoDB)]
```

## Operational Event Flow

```mermaid
sequenceDiagram
    participant Telemetry as OpenSky / Simulator
    participant Flight as flight-service
    participant Kafka as Kafka
    participant Baggage as baggage-service
    participant Cargo as cargo-service
    participant Warehouse as warehouse-service
    participant Inventory as inventory-service
    participant Dashboard as operations-dashboard

    Telemetry->>Flight: Flight telemetry / arrival signal
    Flight->>Kafka: flight.arrival
    Simulator->>Kafka: baggage.scan / cargo.unload / warehouse.receive
    Kafka->>Baggage: baggage.scan
    Kafka->>Cargo: cargo.unload
    Kafka->>Warehouse: warehouse.receive
    Warehouse->>Kafka: inventory.update
    Kafka->>Inventory: inventory.update
    Dashboard->>ApiGateway: UI requests
    ApiGateway->>Flight: telemetry / status
    ApiGateway->>Inventory: cache / inventory status
```

## Current Quality Notes
- The repo now has working CI coverage across Java, Python, .NET, Node, Playwright, and Cucumber.
- The canonical paths are under `services/`, `frontend/operations-dashboard/`, and `infrastructure/`.
- Some services such as `worker-service` and `analytics-service` are still placeholders and are documented as planned components rather than full implementations.
- The repo still contains a few older exploratory folders outside the canonical plan; the README and repository-structure docs identify which paths are authoritative.
