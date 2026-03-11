from fastapi.testclient import TestClient

from app.main import app

client = TestClient(app)


def test_healthcheck() -> None:
    response = client.get("/health")

    assert response.status_code == 200
    assert response.json()["service"] == "simulator-service"


def test_generate_baggage_events() -> None:
    response = client.get("/api/simulator/baggage", params={"count": 2})

    assert response.status_code == 200
    payload = response.json()
    assert payload["event_type"] == "baggage.scan"
    assert payload["generated_count"] == 2
    assert len(payload["events"]) == 2


def test_generate_cargo_events() -> None:
    response = client.get("/api/simulator/cargo", params={"count": 1})

    assert response.status_code == 200
    payload = response.json()
    assert payload["event_type"] == "cargo.unload"
    assert payload["generated_count"] == 1


def test_generate_warehouse_events() -> None:
    response = client.get("/api/simulator/warehouse", params={"count": 1})

    assert response.status_code == 200
    payload = response.json()
    assert payload["event_type"] == "warehouse.receive"
    assert payload["generated_count"] == 1
