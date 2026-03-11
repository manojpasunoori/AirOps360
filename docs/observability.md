# Observability

AirOps360 now exposes a first Prometheus metrics surface across the currently scaffolded services.

## Metrics endpoints
- `flight-service`: `http://<host>:8081/actuator/prometheus`
- `warehouse-service`: `http://<host>:8082/actuator/prometheus`
- `inventory-service`: `http://<host>:8083/actuator/prometheus`
- `cargo-service`: `http://<host>:8000/metrics`
- `simulator-service`: `http://<host>:8001/metrics`
- `api-gateway`: `http://<host>:3000/metrics`

## Implementation details
- Spring Boot services use Actuator with the Prometheus Micrometer registry.
- FastAPI services use `prometheus-fastapi-instrumentator`.
- The Koa gateway uses `prom-client` with default Node.js process metrics.

## Prometheus starter config
A starter scrape configuration is available at `infrastructure/docker/prometheus/prometheus.yml`.
It targets the current service names and ports used by the local AirOps360 stack.

## Notes
- `baggage-service`, `operations-dashboard`, and infrastructure dependencies are not instrumented in this commit.
- Grafana dashboards are intentionally deferred to the next step in the plan.
