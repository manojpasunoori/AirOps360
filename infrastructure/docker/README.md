# docker

This folder contains local container environment guidance for AirOps360.

## Commit 2 scope

The local Docker environment currently provisions the shared stateful dependencies required by the platform:
- PostgreSQL for operational data
- Redis for cache-backed workflows
- MongoDB for cargo metadata and flexible documents

Kafka and service containers are intentionally introduced in later commits so each layer can evolve in small steps.

## Local usage

From the repository root:

```bash
docker compose up -d
docker compose ps
```

To customize credentials or ports, copy the root `.env.example` to `.env` and adjust the values before starting the stack.

## Planned next steps

- Commit 3: Kafka and topic-oriented local messaging setup
- Commit 4: PostgreSQL schema bootstrap assets
- Later commits: service containers and developer workflows
