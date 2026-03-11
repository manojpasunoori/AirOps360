from pydantic import BaseModel, Field


class CargoUnloadRequest(BaseModel):
    manifest_reference: str = Field(min_length=3, max_length=40)
    flight_number: str = Field(min_length=2, max_length=16)
    origin_airport: str = Field(min_length=3, max_length=3)
    destination_airport: str = Field(min_length=3, max_length=3)
    cargo_type: str = Field(min_length=2, max_length=40)
    pieces: int = Field(ge=1)
    weight_kg: float = Field(gt=0)
    priority: str = Field(default="STANDARD")


class CargoUnloadResponse(BaseModel):
    manifest_reference: str
    flight_number: str
    status: str
    next_step: str
