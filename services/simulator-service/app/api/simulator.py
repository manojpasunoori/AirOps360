from fastapi import APIRouter, Query

from app.config import get_settings
from app.generator import next_reference, utc_now, utc_plus
from app.models import (
    BaggageScanEvent,
    CargoUnloadEvent,
    SimulatorBatchResponse,
    WarehouseReceiveEvent,
)

router = APIRouter(prefix="/api/simulator", tags=["simulator"])


@router.get("/baggage", response_model=SimulatorBatchResponse)
def generate_baggage_events(count: int = Query(default=3, ge=1, le=20)) -> SimulatorBatchResponse:
    settings = get_settings()
    events = []
    for index in range(count):
        events.append(
            BaggageScanEvent(
                bag_tag=next_reference("BG"),
                flight_number=f"{settings.default_airline}{100 + index}",
                scan_point=f"SORTER-{index + 1}",
                scan_status="SCREENED",
                scanned_at=utc_plus(index),
            ).model_dump(mode="json")
        )

    return SimulatorBatchResponse(
        event_type="baggage.scan",
        generated_count=count,
        generated_at=utc_now(),
        events=events,
    )


@router.get("/cargo", response_model=SimulatorBatchResponse)
def generate_cargo_events(count: int = Query(default=2, ge=1, le=20)) -> SimulatorBatchResponse:
    settings = get_settings()
    events = []
    for index in range(count):
        events.append(
            CargoUnloadEvent(
                manifest_reference=next_reference("CM"),
                flight_number=f"{settings.default_airline}{200 + index}",
                cargo_type="BULK",
                pieces=10 + index,
                weight_kg=450.0 + (index * 25),
                unloaded_at=utc_plus(index * 3),
            ).model_dump(mode="json")
        )

    return SimulatorBatchResponse(
        event_type="cargo.unload",
        generated_count=count,
        generated_at=utc_now(),
        events=events,
    )


@router.get("/warehouse", response_model=SimulatorBatchResponse)
def generate_warehouse_events(count: int = Query(default=2, ge=1, le=20)) -> SimulatorBatchResponse:
    events = []
    for index in range(count):
        events.append(
            WarehouseReceiveEvent(
                manifest_reference=next_reference("WR"),
                sku=f"SKU-{300 + index}",
                storage_zone=f"ZONE-{chr(65 + index)}",
                quantity=15 + index,
                received_at=utc_plus(index * 5),
            ).model_dump(mode="json")
        )

    return SimulatorBatchResponse(
        event_type="warehouse.receive",
        generated_count=count,
        generated_at=utc_now(),
        events=events,
    )
