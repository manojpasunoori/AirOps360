from fastapi import APIRouter, status

from app.models import CargoUnloadRequest, CargoUnloadResponse

router = APIRouter(prefix="/api/cargo", tags=["cargo"])


@router.post("/unload", response_model=CargoUnloadResponse, status_code=status.HTTP_202_ACCEPTED)
def register_unload(payload: CargoUnloadRequest) -> CargoUnloadResponse:
    return CargoUnloadResponse(
        manifest_reference=payload.manifest_reference,
        flight_number=payload.flight_number,
        status="RECEIVED",
        next_step="warehouse.receive",
    )
