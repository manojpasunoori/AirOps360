from datetime import datetime

from pydantic import BaseModel, Field


class BaggageScanEvent(BaseModel):
    bag_tag: str
    flight_number: str
    scan_point: str
    scan_status: str
    scanned_at: datetime


class CargoUnloadEvent(BaseModel):
    manifest_reference: str
    flight_number: str
    cargo_type: str
    pieces: int = Field(ge=1)
    weight_kg: float = Field(gt=0)
    unloaded_at: datetime


class WarehouseReceiveEvent(BaseModel):
    manifest_reference: str
    sku: str
    storage_zone: str
    quantity: int = Field(ge=1)
    received_at: datetime


class SimulatorBatchResponse(BaseModel):
    event_type: str
    generated_count: int
    generated_at: datetime
    events: list[dict]
