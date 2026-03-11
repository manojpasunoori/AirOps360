# Kafka Topics

This file captures the initial local topic plan for AirOps360.

| Topic | Purpose | Schema |
| --- | --- | --- |
| `flight.arrival` | normalized flight arrival events from telemetry ingestion | `docs/event-schemas/flight-arrival.schema.json` |
| `baggage.scan` | baggage scan updates emitted by the baggage service | `docs/event-schemas/baggage-scan.schema.json` |
| `cargo.unload` | cargo unloading events emitted by the cargo workflow | `docs/event-schemas/cargo-unload.schema.json` |
| `warehouse.receive` | warehouse receiving events emitted after cargo intake | `docs/event-schemas/warehouse-receive.schema.json` |
| `inventory.update` | inventory change events for downstream consumers and cache sync | `docs/event-schemas/inventory-update.schema.json` |

## Local bootstrap behavior

The `kafka-init` container in `docker-compose.yml` creates these topics automatically for local development.

## Contract notes

- Event payloads use JSON for the current local development flow.
- Each schema requires `eventId`, `eventType`, and `occurredAt` for traceability.
- Producers should keep `eventType` aligned with the topic name.
