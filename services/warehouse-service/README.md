# warehouse-service

Spring Boot service responsible for warehouse receiving operations.

## Commit 8 scope

This service now includes a minimal Spring Boot skeleton with:
- Maven build configuration
- Application bootstrap entrypoint
- Warehouse receive endpoint at `/api/warehouse/receive`
- Lightweight status endpoint at `/api/warehouse/status`
- Basic application configuration
- Context startup test

## Planned responsibilities
- Consume cargo unload updates
- Register warehouse receiving events
- Publish `warehouse.receive` events to Kafka
