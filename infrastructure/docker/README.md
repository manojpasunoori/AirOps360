# docker

This folder contains local container environment guidance for AirOps360.

## Commit 24 scope
The repository now includes service-level Dockerfiles plus starter observability assets for Prometheus and Grafana:
- `services/flight-service/Dockerfile`
- `services/cargo-service/Dockerfile`
- `services/warehouse-service/Dockerfile`
- `services/inventory-service/Dockerfile`
- `services/baggage-service/Dockerfile`
- `services/simulator-service/Dockerfile`
- `services/api-gateway/Dockerfile`
- `infrastructure/docker/prometheus/prometheus.yml`
- `infrastructure/docker/grafana/`

These files are intentionally minimal and match each service's current runtime stack and observability surface.

## Local usage

Build an individual service image from the repository root, for example:

```bash
docker build -t airops360-flight-service ./services/flight-service
docker build -t airops360-api-gateway ./services/api-gateway
```

Grafana provisioning assets expect:
- Prometheus reachable at `http://prometheus:9090`
- dashboard JSON mounted from `infrastructure/docker/grafana/dashboards`
- provisioning config mounted from `infrastructure/docker/grafana/provisioning`

## Notes

- Java services use multi-stage Maven + JRE images.
- Python services install from `requirements.txt` and run with Uvicorn.
- The .NET baggage worker uses SDK publish and a runtime-only final image.
- The Node gateway installs runtime dependencies directly from `package.json`.
- Grafana dashboard provisioning is prepared here; container wiring can be layered into Compose later if needed.
