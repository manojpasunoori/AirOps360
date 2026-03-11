# AirOps360 Repository Structure

This document captures the canonical repository layout for the AirOps360 platform.

## Services
- `services/flight-service` - Spring Boot flight telemetry ingestion and arrival event publishing
- `services/baggage-service` - .NET worker for baggage scan processing
- `services/cargo-service` - FastAPI cargo unload workflow service
- `services/warehouse-service` - Spring Boot warehouse receiving workflow service
- `services/inventory-service` - inventory state and cache synchronization service
- `services/worker-service` - worker assignment and task orchestration service
- `services/simulator-service` - synthetic event generator for local development and demos
- `services/analytics-service` - operational KPI aggregation and analytics
- `services/api-gateway` - Koa gateway for dashboard and API clients

## Frontend
- `frontend/operations-dashboard` - React and TypeScript operations dashboard

## Infrastructure
- `infrastructure/docker` - local container orchestration assets
- `infrastructure/kubernetes` - manifests and overlays for cluster deployment
- `infrastructure/argocd` - GitOps application definitions

## Documentation
- `docs/README.md` - documentation index
- `docs/repository-structure.md` - canonical layout reference

## Notes
- The repository may contain older or experimental folders outside this list.
- The directories above are the canonical paths for the 25-commit implementation plan.
