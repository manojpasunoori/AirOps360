# flight-service

Spring Boot service responsible for ingesting and publishing flight arrival events.

## Commit 6 scope

This service now includes a first OpenSky integration slice with:
- External API configuration for OpenSky Network
- HTTP client for the `/api/states/all` endpoint
- Telemetry normalization service for selected flight state fields
- Read-only endpoint at `/api/flights/telemetry`
- Normalization tests around OpenSky state mapping

## Planned responsibilities
- Consume flight telemetry from free public APIs
- Normalize arrival updates into Kafka events
- Persist flights to PostgreSQL
