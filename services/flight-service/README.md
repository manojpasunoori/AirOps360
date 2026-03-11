# flight-service

Spring Boot service responsible for ingesting and publishing flight arrival events.

## Commit 5 scope

This service now includes a minimal Spring Boot skeleton with:
- Maven build configuration
- Application bootstrap entrypoint
- Lightweight status endpoint at `/api/flights/status`
- Basic application configuration
- Context startup test

## Planned responsibilities
- Consume flight telemetry from free public APIs
- Normalize arrival updates into Kafka events
- Persist flight records to PostgreSQL
