from fastapi.testclient import TestClient

from app.main import app

client = TestClient(app)


def test_healthcheck() -> None:
    response = client.get("/health")

    assert response.status_code == 200
    assert response.json()["service"] == "cargo-service"


def test_register_unload() -> None:
    response = client.post(
        "/api/cargo/unload",
        json={
            "manifest_reference": "CM-2001",
            "flight_number": "AA101",
            "origin_airport": "DFW",
            "destination_airport": "ORD",
            "cargo_type": "BULK",
            "pieces": 12,
            "weight_kg": 640.5,
            "priority": "STANDARD",
        },
    )

    assert response.status_code == 202
    assert response.json()["status"] == "RECEIVED"
    assert response.json()["next_step"] == "warehouse.receive"
