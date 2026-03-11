-- AirOps360 PostgreSQL bootstrap
-- Commit 4: foundational operational schema

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS flights (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    flight_number VARCHAR(16) NOT NULL UNIQUE,
    airline_code VARCHAR(8) NOT NULL,
    origin_airport CHAR(3) NOT NULL,
    destination_airport CHAR(3) NOT NULL,
    scheduled_arrival TIMESTAMPTZ NOT NULL,
    estimated_arrival TIMESTAMPTZ,
    actual_arrival TIMESTAMPTZ,
    status VARCHAR(24) NOT NULL DEFAULT 'SCHEDULED',
    gate VARCHAR(12),
    source_system VARCHAR(32) NOT NULL DEFAULT 'SIMULATOR',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_flights_status CHECK (status IN ('SCHEDULED', 'EN_ROUTE', 'LANDED', 'ARRIVED', 'DELAYED', 'CANCELLED'))
);

CREATE TABLE IF NOT EXISTS baggage_scans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bag_tag VARCHAR(32) NOT NULL,
    flight_number VARCHAR(16) NOT NULL,
    scan_location VARCHAR(64) NOT NULL,
    scan_status VARCHAR(24) NOT NULL,
    event_time TIMESTAMPTZ NOT NULL,
    passenger_reference VARCHAR(64),
    worker_id VARCHAR(32),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_baggage_scan_status CHECK (scan_status IN ('RECEIVED', 'SCREENED', 'SORTED', 'LOADED', 'MISROUTED', 'ARRIVED'))
);

CREATE TABLE IF NOT EXISTS cargo_manifest (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    manifest_reference VARCHAR(40) NOT NULL UNIQUE,
    flight_number VARCHAR(16) NOT NULL,
    cargo_type VARCHAR(40) NOT NULL,
    origin_airport CHAR(3) NOT NULL,
    destination_airport CHAR(3) NOT NULL,
    weight_kg NUMERIC(10, 2) NOT NULL,
    pieces INTEGER NOT NULL DEFAULT 1,
    priority VARCHAR(16) NOT NULL DEFAULT 'STANDARD',
    status VARCHAR(24) NOT NULL DEFAULT 'PENDING',
    received_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_cargo_priority CHECK (priority IN ('STANDARD', 'EXPRESS', 'CRITICAL')),
    CONSTRAINT chk_cargo_status CHECK (status IN ('PENDING', 'UNLOADED', 'RECEIVED', 'STAGED', 'CLOSED'))
);

CREATE TABLE IF NOT EXISTS warehouse_inventory (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sku VARCHAR(40) NOT NULL UNIQUE,
    item_name VARCHAR(120) NOT NULL,
    category VARCHAR(40) NOT NULL,
    quantity_on_hand INTEGER NOT NULL DEFAULT 0,
    unit_of_measure VARCHAR(16) NOT NULL DEFAULT 'EA',
    storage_zone VARCHAR(24) NOT NULL,
    last_received_at TIMESTAMPTZ,
    last_updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    reorder_threshold INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT chk_inventory_quantity CHECK (quantity_on_hand >= 0),
    CONSTRAINT chk_inventory_reorder CHECK (reorder_threshold >= 0)
);

CREATE TABLE IF NOT EXISTS worker_tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    worker_id VARCHAR(32) NOT NULL,
    task_type VARCHAR(32) NOT NULL,
    task_reference VARCHAR(64) NOT NULL,
    assigned_area VARCHAR(32) NOT NULL,
    priority VARCHAR(16) NOT NULL DEFAULT 'NORMAL',
    status VARCHAR(24) NOT NULL DEFAULT 'PENDING',
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_worker_priority CHECK (priority IN ('LOW', 'NORMAL', 'HIGH', 'CRITICAL')),
    CONSTRAINT chk_worker_status CHECK (status IN ('PENDING', 'ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'))
);

CREATE INDEX IF NOT EXISTS idx_flights_destination_status ON flights (destination_airport, status);
CREATE INDEX IF NOT EXISTS idx_flights_arrival_time ON flights (scheduled_arrival);
CREATE INDEX IF NOT EXISTS idx_baggage_scans_tag_time ON baggage_scans (bag_tag, event_time DESC);
CREATE INDEX IF NOT EXISTS idx_baggage_scans_flight ON baggage_scans (flight_number);
CREATE INDEX IF NOT EXISTS idx_cargo_manifest_flight_status ON cargo_manifest (flight_number, status);
CREATE INDEX IF NOT EXISTS idx_warehouse_inventory_zone ON warehouse_inventory (storage_zone);
CREATE INDEX IF NOT EXISTS idx_worker_tasks_status_priority ON worker_tasks (status, priority);
CREATE INDEX IF NOT EXISTS idx_worker_tasks_worker ON worker_tasks (worker_id);

INSERT INTO flights (
    flight_number,
    airline_code,
    origin_airport,
    destination_airport,
    scheduled_arrival,
    estimated_arrival,
    status,
    gate,
    source_system
) VALUES
    ('AA101', 'AA', 'DFW', 'ORD', NOW() + INTERVAL '2 hours', NOW() + INTERVAL '2 hours 15 minutes', 'EN_ROUTE', 'B12', 'SIMULATOR'),
    ('UA220', 'UA', 'DEN', 'ORD', NOW() + INTERVAL '3 hours', NOW() + INTERVAL '3 hours 5 minutes', 'SCHEDULED', 'C7', 'SIMULATOR')
ON CONFLICT (flight_number) DO NOTHING;

INSERT INTO cargo_manifest (
    manifest_reference,
    flight_number,
    cargo_type,
    origin_airport,
    destination_airport,
    weight_kg,
    pieces,
    priority,
    status,
    received_at
) VALUES
    ('CM-1001', 'AA101', 'BULK', 'DFW', 'ORD', 1250.50, 18, 'STANDARD', 'PENDING', NULL),
    ('CM-1002', 'UA220', 'MEDICAL', 'DEN', 'ORD', 320.00, 4, 'CRITICAL', 'PENDING', NULL)
ON CONFLICT (manifest_reference) DO NOTHING;

INSERT INTO warehouse_inventory (
    sku,
    item_name,
    category,
    quantity_on_hand,
    unit_of_measure,
    storage_zone,
    last_received_at,
    reorder_threshold
) VALUES
    ('ULD-NET-01', 'Cargo Restraint Net', 'GROUND_SUPPORT', 24, 'EA', 'ZONE-A', NOW() - INTERVAL '1 day', 6),
    ('BAG-TAG-STD', 'Standard Baggage Tags', 'CONSUMABLE', 5000, 'EA', 'ZONE-B', NOW() - INTERVAL '6 hours', 1000)
ON CONFLICT (sku) DO NOTHING;

INSERT INTO worker_tasks (
    worker_id,
    task_type,
    task_reference,
    assigned_area,
    priority,
    status
) VALUES
    ('WRK-001', 'FLIGHT_TURN', 'AA101', 'RAMP', 'HIGH', 'ASSIGNED'),
    ('WRK-014', 'WAREHOUSE_RECEIVE', 'CM-1001', 'WAREHOUSE', 'NORMAL', 'PENDING')
ON CONFLICT DO NOTHING;
