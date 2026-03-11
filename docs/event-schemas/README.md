# Event Schemas

This folder contains the canonical JSON schemas for the AirOps360 Kafka topics.

## Current contracts
- `flight-arrival.schema.json`
- `baggage-scan.schema.json`
- `cargo-unload.schema.json`
- `warehouse-receive.schema.json`
- `inventory-update.schema.json`

## Notes
- These schemas are designed for early local development and service integration.
- All event timestamps use ISO 8601 UTC strings.
- Each event includes an `eventId` and `eventType` so producers and consumers can trace messages consistently.
