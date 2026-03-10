# ✈️ AirOps360 — Ground & Warehouse Operations Management Platform

![CI/CD](https://github.com/manojpasunoori/AirOps360/actions/workflows/ci-cd.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)
![Python](https://img.shields.io/badge/Python-3.12-blue)
![C#](https://img.shields.io/badge/C%23-.NET%208-purple)
![Node.js](https://img.shields.io/badge/KOA-Node.js%2020-brightgreen)
![Azure AKS](https://img.shields.io/badge/Azure-AKS-0089D6)
![Kafka](https://img.shields.io/badge/Apache-Kafka-231F20)

> A **production-grade, polyglot microservices platform** modeling the operational software that keeps airline ground operations running — from warehouse inventory management and baggage scan event processing to ground crew scheduling and real-time cargo tracking.

Built on the **exact American Airlines technology stack**: Java/Spring Boot, Python/FastAPI, C#/.NET 8, KOA (Node.js) API gateway, Apache Kafka, Azure AKS, PostgreSQL with stored procedures, Redis, Dynatrace APM, Mezmo log streaming, and a full DevSecOps pipeline via GitHub Actions + Azure DevOps + ArgoCD.

---

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                    React Dashboard                       │
│                  (Port 3001)                             │
└───────────────────────┬─────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────┐
│           KOA API Gateway  (Port 3000)                   │
│   JWT Auth · Rate Limiting · GraphQL · REST Proxy        │
└──┬──────────────┬──────────────┬───────────────┬─────────┘
   │              │              │               │
   ▼              ▼              ▼               ▼
┌──────┐    ┌──────────┐   ┌─────────┐   ┌──────────┐
│ WMS  │    │  Cargo   │   │  Crew   │   │ Baggage  │
│:8081 │    │ :8082    │   │ :8083   │   │ Worker   │
│Java  │    │  Java    │   │ Python  │   │ C#/.NET  │
│Spring│    │  Spring  │   │ FastAPI │   │ :8084    │
└──┬───┘    └────┬─────┘   └────┬────┘   └────┬─────┘
   │              │              │               │
   └──────────────┴──────────────┴───────────────┘
                        │
            ┌───────────▼───────────┐
            │     Apache Kafka      │
            │  inventory_events     │
            │  baggage_scan         │
            │  crew_alerts          │
            │  shipment_events      │
            └───────────┬───────────┘
                        │
         ┌──────────────┼──────────────┐
         ▼              ▼              ▼
    PostgreSQL        MongoDB        Redis
   (WMS, Crew,      (Dynamic       (Cache,
   Baggage, Cargo)   Config)       Sessions)
```

---

## 🚀 Quick Start (Local — 5 minutes)

### Prerequisites
- Docker Desktop
- Java 17, Python 3.12, .NET 8, Node.js 20

### Run Everything

```bash
git clone https://github.com/manojpasunoori/AirOps360.git
cd AirOps360

# Start all infrastructure + services
docker compose up -d

# Verify all services are healthy
docker compose ps
```

### Access Points

| Service | URL | Description |
|---|---|---|
| KOA API Gateway | http://localhost:3000 | Main entry point |
| WMS Swagger UI | http://localhost:8081/swagger-ui.html | Inventory API docs |
| Crew Scheduler Docs | http://localhost:8083/swagger-ui.html | Crew API docs |
| React Dashboard | http://localhost:3001 | Operations dashboard |
| GraphQL Playground | http://localhost:3000/graphql | GraphQL explorer |
| Kafka UI | http://localhost:8090 | Monitor Kafka topics |
| Grafana | http://localhost:3002 | Metrics dashboards |
| Prometheus | http://localhost:9090 | Raw metrics |

### Demo Login (API Gateway)
```bash
curl -X POST http://localhost:3000/auth/login \
  -H "Content-Type: application/json" \
  -d '{"employeeId": "ADMIN", "password": "admin123"}'
```

---

## 📦 Services

| Service | Language | Port | Responsibility |
|---|---|---|---|
| `koa-api-gateway` | Node.js / KOA | 3000 | JWT auth, rate limiting, GraphQL, REST proxy |
| `wms-inventory-service` | Java 17 / Spring Boot | 8081 | Warehouse lifecycle (receive, put-away, pick, ship) |
| `cargo-tracking-service` | Java 17 / Spring Boot | 8082 | Cargo shipment tracking and events |
| `crew-scheduler-service` | Python 3.12 / FastAPI | 8083 | Ground crew scheduling and shift management |
| `baggage-worker-service` | C# / .NET 8 | 8084 | Baggage scan event processing, status tracking |
| `notification-service` | Python / FastAPI | 8085 | Alert fan-out via Kafka |
| `react-dashboard` | React / TypeScript | 3001 | Real-time ops dashboard |

---

## 🗄️ WMS Workflows (Advantage Architecture Patterns)

The WMS Inventory Service implements full warehouse lifecycle management via **PostgreSQL stored procedures**:

```sql
-- Receive items into warehouse
SELECT wms.receive_items('SKU-001', 'Airline Blanket', 'CABIN', 100, location_id, 'EMP001', 'REF-001');

-- Pick items for shipment
SELECT wms.pick_items('SKU-001', 5, 'EMP002', 'SHIP-REF-001');

-- Inventory summary by zone
SELECT * FROM wms.get_inventory_summary();
```

**Workflows covered:** Receive → Put-Away → Pick → Ship → Adjust — directly modeling Swisslog AutoStore and enterprise WMS operational patterns.

---

## 🧪 Testing

```bash
# Java unit tests (JUnit + Mockito + TestNG)
cd services/wms-inventory-service && mvn test

# C# tests (NUnit + Moq + FluentAssertions)
cd services/baggage-worker-service && dotnet test

# Python tests (PyTest)
cd services/crew-scheduler-service && pytest tests/ -v

# E2E Playwright tests
cd tests/e2e-playwright && npx playwright test

# BDD Cucumber tests
cd tests/bdd-cucumber && npm test
```

---

## 📊 Observability Stack

- **Dynatrace APM** — distributed tracing across all 9 services
- **Mezmo** — centralized log streaming and search
- **Prometheus + Grafana** — metrics dashboards (SLA/SLO tracking)
- **SonarQube** — SAST quality gates (blocks merges on Critical issues)
- **Trivy** — container vulnerability scanning (blocks on Critical/High CVEs)

---

## ☁️ Azure Deployment

```bash
# Deploy to Azure AKS
kubectl apply -k infra/k8s/overlays/prod

# Or via ArgoCD GitOps
argocd app sync airops360
```

**Azure services used:** AKS, Azure Service Bus, Azure SQL, Key Vault, Azure Monitor, Mezmo integration, Azure DevOps pipelines

---

## 📐 Architecture Decisions

See [`docs/adr/`](docs/adr/) for Architecture Decision Records:
- ADR-001: Polyglot microservices (Java + Python + C# + Node.js)
- ADR-002: KOA as API gateway over Spring Cloud Gateway
- ADR-003: Kafka for async event streaming
- ADR-004: PostgreSQL stored procedures for WMS workflows
- ADR-005: GitOps with ArgoCD for zero-drift deployments

---

## 🔗 Related Projects

- [AeroStream](https://github.com/manojpasunoori/AeroStream) — Real-time airline operations intelligence platform
- [Secure K8s Platform](https://github.com/manojpasunoori/Secure-k8s-microservice-platform) — Production-grade DevSecOps pipeline

---

*Built by [Manoj Pasunoori](https://linkedin.com/in/manoj-pasunoori-b5673a190) · Arlington, TX*
