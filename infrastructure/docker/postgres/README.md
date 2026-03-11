# PostgreSQL Schema

This directory contains the Docker bootstrap SQL for AirOps360 PostgreSQL initialization.

## Bootstrap files
- `init/001-init.sql` - creates the canonical operational tables and lightweight seed data

## Tables created
- `flights`
- `baggage_scans`
- `cargo_manifest`
- `warehouse_inventory`
- `worker_tasks`

## Notes
- The schema uses `pgcrypto` for UUID generation.
- Seed records are included for local development and can be safely re-run because inserts use conflict guards where appropriate.
