# inventory-service

Service responsible for warehouse inventory state and cache synchronization.

## Commit 9 scope

This service now includes a minimal Spring Boot Redis caching slice with:
- Maven build configuration
- Application bootstrap entrypoint
- Redis-backed inventory cache service
- Cache read/write endpoints under `/api/inventory/cache`
- Basic application configuration for local Redis connectivity
- Targeted service tests for cache serialization behavior

## Planned responsibilities
- Consume warehouse receiving events
- Persist inventory changes
- Maintain Redis-backed inventory cache
