# cargo-service

FastAPI service responsible for cargo unload workflows.

## Commit 7 scope

This service now includes a minimal FastAPI skeleton with:
- dependency manifest in `requirements.txt`
- service settings and basic app bootstrap
- health endpoint at `/health`
- cargo unload endpoint at `/api/cargo/unload`
- basic API tests using FastAPI TestClient

## Planned responsibilities
- Receive cargo unload inputs
- Persist cargo metadata to MongoDB
- Publish `cargo.unload` events to Kafka
