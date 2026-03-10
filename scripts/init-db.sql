-- ============================================================
-- AirOps360 — Database Initialization Script
-- Creates all schemas, tables, stored procedures, and seed data
-- ============================================================

-- ── SCHEMAS ──────────────────────────────────────────────────────
CREATE SCHEMA IF NOT EXISTS wms;
CREATE SCHEMA IF NOT EXISTS cargo;
CREATE SCHEMA IF NOT EXISTS crew;
CREATE SCHEMA IF NOT EXISTS baggage;

-- ── WMS TABLES ───────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS wms.locations (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    zone        VARCHAR(10) NOT NULL,
    aisle       VARCHAR(10) NOT NULL,
    bay         VARCHAR(10) NOT NULL,
    level       VARCHAR(10) NOT NULL,
    capacity    INTEGER NOT NULL DEFAULT 100,
    occupied    INTEGER NOT NULL DEFAULT 0,
    created_at  TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS wms.inventory_items (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sku             VARCHAR(50) UNIQUE NOT NULL,
    description     VARCHAR(255) NOT NULL,
    category        VARCHAR(50) NOT NULL,
    quantity        INTEGER NOT NULL DEFAULT 0,
    unit_weight_kg  DECIMAL(10,3),
    location_id     UUID REFERENCES wms.locations(id),
    status          VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE'
                        CHECK (status IN ('AVAILABLE','RESERVED','QUARANTINE','DISPOSED')),
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS wms.warehouse_transactions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_type VARCHAR(20) NOT NULL
                        CHECK (transaction_type IN ('RECEIVE','PUT_AWAY','PICK','SHIP','ADJUST')),
    item_id         UUID REFERENCES wms.inventory_items(id),
    quantity        INTEGER NOT NULL,
    from_location   UUID REFERENCES wms.locations(id),
    to_location     UUID REFERENCES wms.locations(id),
    reference_no    VARCHAR(100),
    operator_id     VARCHAR(50),
    notes           TEXT,
    created_at      TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_wms_items_sku ON wms.inventory_items(sku);
CREATE INDEX idx_wms_items_status ON wms.inventory_items(status);
CREATE INDEX idx_wms_txn_type ON wms.warehouse_transactions(transaction_type);
CREATE INDEX idx_wms_txn_created ON wms.warehouse_transactions(created_at);

-- ── WMS STORED PROCEDURES ────────────────────────────────────────

-- Receive items into warehouse
CREATE OR REPLACE FUNCTION wms.receive_items(
    p_sku           VARCHAR,
    p_description   VARCHAR,
    p_category      VARCHAR,
    p_quantity      INTEGER,
    p_location_id   UUID,
    p_operator_id   VARCHAR,
    p_reference_no  VARCHAR
) RETURNS UUID AS $$
DECLARE
    v_item_id UUID;
BEGIN
    INSERT INTO wms.inventory_items (sku, description, category, quantity, location_id, status)
    VALUES (p_sku, p_description, p_category, p_quantity, p_location_id, 'AVAILABLE')
    ON CONFLICT (sku) DO UPDATE
        SET quantity   = wms.inventory_items.quantity + p_quantity,
            updated_at = NOW()
    RETURNING id INTO v_item_id;

    INSERT INTO wms.warehouse_transactions
        (transaction_type, item_id, quantity, to_location, reference_no, operator_id)
    VALUES ('RECEIVE', v_item_id, p_quantity, p_location_id, p_reference_no, p_operator_id);

    UPDATE wms.locations
    SET occupied = occupied + p_quantity
    WHERE id = p_location_id;

    RETURN v_item_id;
END;
$$ LANGUAGE plpgsql;

-- Pick items for shipment
CREATE OR REPLACE FUNCTION wms.pick_items(
    p_sku           VARCHAR,
    p_quantity      INTEGER,
    p_operator_id   VARCHAR,
    p_reference_no  VARCHAR
) RETURNS BOOLEAN AS $$
DECLARE
    v_item_id   UUID;
    v_available INTEGER;
    v_loc_id    UUID;
BEGIN
    SELECT id, quantity, location_id
    INTO v_item_id, v_available, v_loc_id
    FROM wms.inventory_items
    WHERE sku = p_sku AND status = 'AVAILABLE'
    FOR UPDATE;

    IF v_available < p_quantity THEN
        RAISE EXCEPTION 'Insufficient stock for SKU %: available=%, requested=%',
            p_sku, v_available, p_quantity;
    END IF;

    UPDATE wms.inventory_items
    SET quantity   = quantity - p_quantity,
        status     = CASE WHEN quantity - p_quantity = 0 THEN 'RESERVED' ELSE 'AVAILABLE' END,
        updated_at = NOW()
    WHERE id = v_item_id;

    INSERT INTO wms.warehouse_transactions
        (transaction_type, item_id, quantity, from_location, reference_no, operator_id)
    VALUES ('PICK', v_item_id, p_quantity, v_loc_id, p_reference_no, p_operator_id);

    RETURN TRUE;
END;
$$ LANGUAGE plpgsql;

-- Inventory summary by zone
CREATE OR REPLACE FUNCTION wms.get_inventory_summary()
RETURNS TABLE(zone VARCHAR, total_items BIGINT, total_quantity BIGINT, utilization_pct DECIMAL) AS $$
BEGIN
    RETURN QUERY
    SELECT
        l.zone,
        COUNT(DISTINCT i.id)        AS total_items,
        COALESCE(SUM(i.quantity),0) AS total_quantity,
        ROUND(AVG(l.occupied::DECIMAL / NULLIF(l.capacity,0) * 100), 2) AS utilization_pct
    FROM wms.locations l
    LEFT JOIN wms.inventory_items i ON i.location_id = l.id
    GROUP BY l.zone
    ORDER BY l.zone;
END;
$$ LANGUAGE plpgsql;

-- ── CARGO TABLES ─────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS cargo.shipments (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tracking_no     VARCHAR(50) UNIQUE NOT NULL,
    flight_no       VARCHAR(20),
    origin          CHAR(3) NOT NULL,
    destination     CHAR(3) NOT NULL,
    weight_kg       DECIMAL(10,3) NOT NULL,
    status          VARCHAR(30) NOT NULL DEFAULT 'PENDING'
                        CHECK (status IN ('PENDING','ACCEPTED','LOADED','IN_TRANSIT','ARRIVED','DELIVERED','EXCEPTION')),
    priority        VARCHAR(10) DEFAULT 'STANDARD'
                        CHECK (priority IN ('STANDARD','EXPRESS','CRITICAL')),
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS cargo.tracking_events (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    shipment_id     UUID REFERENCES cargo.shipments(id),
    event_type      VARCHAR(50) NOT NULL,
    location        VARCHAR(100),
    description     TEXT,
    recorded_by     VARCHAR(50),
    event_time      TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_cargo_tracking_no ON cargo.shipments(tracking_no);
CREATE INDEX idx_cargo_status ON cargo.shipments(status);
CREATE INDEX idx_cargo_events_shipment ON cargo.tracking_events(shipment_id);

-- ── CREW TABLES ──────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS crew.staff (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id     VARCHAR(20) UNIQUE NOT NULL,
    full_name       VARCHAR(100) NOT NULL,
    role            VARCHAR(50) NOT NULL,
    certification   VARCHAR(100),
    status          VARCHAR(20) DEFAULT 'ACTIVE'
                        CHECK (status IN ('ACTIVE','ON_LEAVE','INACTIVE')),
    created_at      TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS crew.shifts (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    staff_id        UUID REFERENCES crew.staff(id),
    shift_date      DATE NOT NULL,
    start_time      TIME NOT NULL,
    end_time        TIME NOT NULL,
    gate            VARCHAR(10),
    flight_no       VARCHAR(20),
    status          VARCHAR(20) DEFAULT 'SCHEDULED'
                        CHECK (status IN ('SCHEDULED','IN_PROGRESS','COMPLETED','CANCELLED')),
    created_at      TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_crew_shifts_date ON crew.shifts(shift_date);
CREATE INDEX idx_crew_shifts_staff ON crew.shifts(staff_id);

-- ── BAGGAGE TABLES ───────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS baggage.bags (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tag_no          VARCHAR(20) UNIQUE NOT NULL,
    passenger_ref   VARCHAR(50),
    flight_no       VARCHAR(20) NOT NULL,
    origin          CHAR(3) NOT NULL,
    destination     CHAR(3) NOT NULL,
    weight_kg       DECIMAL(6,3),
    status          VARCHAR(30) DEFAULT 'CHECKED_IN'
                        CHECK (status IN ('CHECKED_IN','SCREENED','LOADED','IN_TRANSIT',
                                          'ARRIVED','DELIVERED','MISSING','DAMAGED')),
    created_at      TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS baggage.scan_events (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bag_id          UUID REFERENCES baggage.bags(id),
    scan_point      VARCHAR(100) NOT NULL,
    scan_status     VARCHAR(30) NOT NULL,
    scanned_by      VARCHAR(50),
    scan_time       TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_baggage_tag ON baggage.bags(tag_no);
CREATE INDEX idx_baggage_flight ON baggage.bags(flight_no);
CREATE INDEX idx_scan_events_bag ON baggage.scan_events(bag_id);

-- ── SEED DATA ────────────────────────────────────────────────────

INSERT INTO wms.locations (zone, aisle, bay, level, capacity) VALUES
    ('A','A1','01','L1', 200), ('A','A1','02','L1', 200),
    ('A','A2','01','L1', 150), ('A','A2','02','L2', 150),
    ('B','B1','01','L1', 300), ('B','B1','02','L1', 300),
    ('C','C1','01','L1', 500), ('C','C1','02','L2', 500)
ON CONFLICT DO NOTHING;

INSERT INTO crew.staff (employee_id, full_name, role, certification) VALUES
    ('EMP001','James Carter','Ground Handler','GH-CERT-A'),
    ('EMP002','Sarah Mitchell','Baggage Handler','BH-CERT-B'),
    ('EMP003','David Park','Cargo Specialist','CS-CERT-A'),
    ('EMP004','Lisa Torres','Crew Supervisor','SUP-CERT-A'),
    ('EMP005','Marcus Johnson','Ground Handler','GH-CERT-B')
ON CONFLICT DO NOTHING;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA wms TO airops;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA cargo TO airops;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA crew TO airops;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA baggage TO airops;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA wms TO airops;
