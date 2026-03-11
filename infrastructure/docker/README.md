# docker

This folder contains local container environment guidance for AirOps360.

## Commit 18 scope

The repository now includes service-level Dockerfiles for the canonical AirOps360 scaffolds created so far:
- `services/flight-service/Dockerfile`
- `services/cargo-service/Dockerfile`
- `services/warehouse-service/Dockerfile`
- `services/inventory-service/Dockerfile`
- `services/baggage-service/Dockerfile`
- `services/simulator-service/Dockerfile`
- `services/api-gateway/Dockerfile`

These Dockerfiles are intentionally minimal and match each service's current runtime stack.

## Local usage

Build an individual service image from the repository root, for example:

```bash
docker build -t airops360-flight-service ./services/flight-service
docker build -t airops360-api-gateway ./services/api-gateway
```

## Notes

- Java services use multi-stage Maven + JRE images.
- Python services install from `requirements.txt` and run with Uvicorn.
- The .NET baggage worker uses SDK publish and a runtime-only final image.
- The Node gateway installs runtime dependencies directly from `package.json`.
