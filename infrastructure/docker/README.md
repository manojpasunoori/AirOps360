# docker

This folder contains local container environment guidance for AirOps360.

## Commit 3 scope

The local Docker environment now provisions the shared stateful dependencies and the event backbone required by the platform:
- PostgreSQL for operational data
- Redis for cache-backed workflows
- MongoDB for cargo metadata and flexible documents
- Kafka in single-node KRaft mode for local event streaming
- Kafka UI for topic inspection and debugging

## Local usage

From the repository root:

```bash
docker compose up -d
docker compose ps
```

To customize credentials or ports, copy the root `.env.example` to `.env` and adjust the values before starting the stack.

Kafka UI is exposed at `http://localhost:8080` by default.

## Local topics

The compose stack bootstraps the canonical AirOps360 topics:
- `flight.arrival`
- `baggage.scan`
- `cargo.unload`
- `warehouse.receive`
- `inventory.update`

See `infrastructure/docker/kafka-topics.md` for the topic list and intent.

## Planned next steps

- Commit 4: PostgreSQL schema bootstrap assets
- Later commits: service containers and producer/consumer wiring
