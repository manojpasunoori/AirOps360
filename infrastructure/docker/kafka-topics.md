# Kafka Topics

This file captures the initial local topic plan for AirOps360.

| Topic | Purpose |
| --- | --- |
| `flight.arrival` | normalized flight arrival events from telemetry ingestion |
| `baggage.scan` | baggage scan updates emitted by the baggage service |
| `cargo.unload` | cargo unloading events emitted by the cargo workflow |
| `warehouse.receive` | warehouse receiving events emitted after cargo intake |
| `inventory.update` | inventory change events for downstream consumers and cache sync |

## Local bootstrap behavior

The `kafka-init` container in `docker-compose.yml` creates these topics automatically for local development.
