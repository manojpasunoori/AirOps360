import json
import random
import time
from datetime import UTC, datetime

from kafka import KafkaProducer
from kafka.errors import NoBrokersAvailable

BOOTSTRAP_SERVERS = "kafka:9092"
FLIGHTS = (
    ("AA101", "DFW"),
    ("AA102", "DFW"),
    ("DL330", "ATL"),
    ("UA220", "ORD"),
)
WAREHOUSE_LOCATIONS = ("WH-A1", "WH-B2", "WH-C3", "WH-D4")
SCAN_POINTS = ("belt-1", "belt-3", "sorter-2", "gate-ramp")
INVENTORY_SKUS = ("SKU-1001", "SKU-2044", "SKU-3310", "SKU-7788")


def utc_timestamp() -> str:
    return datetime.now(UTC).isoformat()


def create_producer() -> KafkaProducer:
    while True:
        try:
            producer = KafkaProducer(
                bootstrap_servers=BOOTSTRAP_SERVERS,
                value_serializer=lambda value: json.dumps(value).encode("utf-8"),
                retries=10,
                acks="all",
            )
            print("Simulator started", flush=True)
            return producer
        except NoBrokersAvailable:
            print("Kafka broker not ready yet, retrying in 5 seconds", flush=True)
            time.sleep(5)


def generate_flight_arrival(flight: str, airport: str) -> dict:
    return {
        "flight": flight,
        "airport": airport,
        "status": "arrived",
        "gate": f"G{random.randint(1, 40)}",
        "timestamp": utc_timestamp(),
    }


def generate_baggage_scan(flight: str) -> dict:
    bag_id = f"BG{random.randint(100, 999)}"
    return {
        "bagId": bag_id,
        "flight": flight,
        "status": "scanned",
        "scanPoint": random.choice(SCAN_POINTS),
        "timestamp": utc_timestamp(),
    }


def generate_cargo_unload(flight: str) -> dict:
    cargo_id = f"CRG{random.randint(10, 99)}"
    return {
        "cargoId": cargo_id,
        "flight": flight,
        "status": "unloaded",
        "pieces": random.randint(2, 20),
        "timestamp": utc_timestamp(),
    }


def generate_warehouse_receive(flight: str) -> dict:
    return {
        "warehouseLocation": random.choice(WAREHOUSE_LOCATIONS),
        "flight": flight,
        "status": "received",
        "receivedUnits": random.randint(5, 30),
        "timestamp": utc_timestamp(),
    }


def generate_inventory_update(flight: str) -> dict:
    return {
        "sku": random.choice(INVENTORY_SKUS),
        "flight": flight,
        "status": "updated",
        "delta": random.randint(1, 12),
        "warehouseLocation": random.choice(WAREHOUSE_LOCATIONS),
        "timestamp": utc_timestamp(),
    }


def publish_cycle(producer: KafkaProducer) -> None:
    flight, airport = random.choice(FLIGHTS)
    events = (
        ("flight.arrival", generate_flight_arrival(flight, airport)),
        ("baggage.scan", generate_baggage_scan(flight)),
        ("cargo.unload", generate_cargo_unload(flight)),
        ("warehouse.receive", generate_warehouse_receive(flight)),
        ("inventory.update", generate_inventory_update(flight)),
    )

    for topic, payload in events:
        producer.send(topic, payload)
        if topic == "flight.arrival":
            print(f"Generated flight arrival event {payload['flight']}", flush=True)
        elif topic == "baggage.scan":
            print(f"Generated baggage scan {payload['bagId']}", flush=True)
        elif topic == "cargo.unload":
            print(f"Generated cargo unload {payload['cargoId']}", flush=True)
        elif topic == "warehouse.receive":
            print(
                f"Generated warehouse receive {payload['warehouseLocation']} for {payload['flight']}",
                flush=True,
            )
        elif topic == "inventory.update":
            print(
                f"Generated inventory update {payload['sku']} at {payload['warehouseLocation']}",
                flush=True,
            )

    producer.flush()


def main() -> None:
    producer = create_producer()
    while True:
        publish_cycle(producer)
        time.sleep(random.uniform(2.0, 3.0))


if __name__ == "__main__":
    main()

