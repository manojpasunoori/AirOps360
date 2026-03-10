import pytest
from fastapi.testclient import TestClient
from main import app
from datetime import date, time

client = TestClient(app)


class TestCrewEndpoints:

    def test_get_all_crew_returns_seeded_data(self):
        response = client.get("/api/v1/crew/")
        assert response.status_code == 200
        crew = response.json()
        assert len(crew) >= 3
        employee_ids = [c["employee_id"] for c in crew]
        assert "EMP001" in employee_ids

    def test_get_crew_member_by_id(self):
        response = client.get("/api/v1/crew/EMP001")
        assert response.status_code == 200
        data = response.json()
        assert data["employee_id"] == "EMP001"
        assert data["full_name"] == "James Carter"
        assert data["status"] == "ACTIVE"

    def test_get_unknown_crew_returns_404(self):
        response = client.get("/api/v1/crew/UNKNOWN999")
        assert response.status_code == 404

    def test_create_new_crew_member(self):
        payload = {
            "employee_id": "EMP999",
            "full_name": "Test Engineer",
            "role": "Ground Handler",
            "certification": "GH-TEST"
        }
        response = client.post("/api/v1/crew/", json=payload)
        assert response.status_code == 201
        data = response.json()
        assert data["employee_id"] == "EMP999"
        assert data["status"] == "ACTIVE"

    def test_create_duplicate_crew_returns_409(self):
        payload = {
            "employee_id": "EMP001",  # Already exists
            "full_name": "Duplicate",
            "role": "Handler"
        }
        response = client.post("/api/v1/crew/", json=payload)
        assert response.status_code == 409

    def test_update_crew_status(self):
        response = client.patch("/api/v1/crew/EMP002/status?new_status=ON_LEAVE")
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "ON_LEAVE"


class TestShiftEndpoints:

    def test_create_shift_successfully(self):
        payload = {
            "employee_id": "EMP001",
            "shift_date": "2026-04-01",
            "start_time": "06:00:00",
            "end_time": "14:00:00",
            "gate": "B22",
            "flight_no": "AA1234"
        }
        response = client.post("/api/v1/shifts/", json=payload)
        assert response.status_code == 201
        data = response.json()
        assert data["employee_id"] == "EMP001"
        assert data["status"] == "SCHEDULED"
        assert data["gate"] == "B22"

    def test_duplicate_shift_returns_409(self):
        payload = {
            "employee_id": "EMP004",
            "shift_date": "2026-04-02",
            "start_time": "06:00:00",
            "end_time": "14:00:00",
        }
        client.post("/api/v1/shifts/", json=payload)
        response = client.post("/api/v1/shifts/", json=payload)
        assert response.status_code == 409

    def test_get_coverage_summary(self):
        response = client.get("/api/v1/shifts/coverage/2026-04-01")
        assert response.status_code == 200
        data = response.json()
        assert "total_shifts" in data
        assert "by_status" in data


class TestHealthEndpoint:

    def test_health_returns_up(self):
        response = client.get("/health/")
        assert response.status_code == 200
        assert response.json()["status"] == "UP"
        assert response.json()["service"] == "crew-scheduler-service"
