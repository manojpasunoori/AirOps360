# simulator-service

Synthetic event generator for baggage, cargo, and warehouse operations.

## Commit 11 scope

This service now includes a minimal FastAPI simulator with:
- dependency manifest in `requirements.txt`
- service settings and app bootstrap
- synthetic event generation endpoints for baggage, cargo, and warehouse flows
- deterministic ID and timestamp generation helpers
- basic API tests using FastAPI TestClient

## Planned responsibilities
- Generate baggage scan events
- Generate cargo unload events
- Generate warehouse receive events
