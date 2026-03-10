from fastapi import APIRouter, HTTPException, status
from pydantic import BaseModel, Field
from typing import Optional
from uuid import UUID, uuid4
from datetime import date, time, datetime
from enum import Enum

router = APIRouter()

class ShiftStatus(str, Enum):
    SCHEDULED = "SCHEDULED"
    IN_PROGRESS = "IN_PROGRESS"
    COMPLETED = "COMPLETED"
    CANCELLED = "CANCELLED"

class ShiftCreate(BaseModel):
    employee_id: str
    shift_date: date
    start_time: time
    end_time: time
    gate: Optional[str] = None
    flight_no: Optional[str] = None

class ShiftResponse(BaseModel):
    id: UUID
    employee_id: str
    shift_date: date
    start_time: time
    end_time: time
    gate: Optional[str]
    flight_no: Optional[str]
    status: ShiftStatus
    created_at: datetime

# In-memory store
_shifts_db: dict[str, dict] = {}

@router.get("/", response_model=list[ShiftResponse])
async def get_all_shifts():
    return list(_shifts_db.values())

@router.get("/date/{shift_date}", response_model=list[ShiftResponse])
async def get_shifts_by_date(shift_date: date):
    return [s for s in _shifts_db.values() if s["shift_date"] == shift_date]

@router.get("/employee/{employee_id}", response_model=list[ShiftResponse])
async def get_shifts_by_employee(employee_id: str):
    return [s for s in _shifts_db.values() if s["employee_id"] == employee_id]

@router.post("/", response_model=ShiftResponse, status_code=status.HTTP_201_CREATED)
async def create_shift(shift: ShiftCreate):
    # Conflict check — employee already has shift on this date
    conflict = any(
        s["employee_id"] == shift.employee_id and s["shift_date"] == shift.shift_date
        for s in _shifts_db.values()
        if s["status"] not in [ShiftStatus.CANCELLED, ShiftStatus.COMPLETED]
    )
    if conflict:
        raise HTTPException(
            status_code=409,
            detail=f"Employee {shift.employee_id} already has a shift on {shift.shift_date}"
        )

    shift_id = str(uuid4())
    new_shift = {
        "id": UUID(shift_id),
        "employee_id": shift.employee_id,
        "shift_date": shift.shift_date,
        "start_time": shift.start_time,
        "end_time": shift.end_time,
        "gate": shift.gate,
        "flight_no": shift.flight_no,
        "status": ShiftStatus.SCHEDULED,
        "created_at": datetime.utcnow(),
    }
    _shifts_db[shift_id] = new_shift
    return new_shift

@router.patch("/{shift_id}/status")
async def update_shift_status(shift_id: str, new_status: ShiftStatus):
    shift = _shifts_db.get(shift_id)
    if not shift:
        raise HTTPException(status_code=404, detail=f"Shift {shift_id} not found")
    shift["status"] = new_status
    return {"shift_id": shift_id, "status": new_status}

@router.get("/coverage/{shift_date}")
async def get_coverage_summary(shift_date: date):
    """Get staffing coverage summary for a given date"""
    day_shifts = [s for s in _shifts_db.values() if s["shift_date"] == shift_date]
    by_status = {}
    for shift in day_shifts:
        st = shift["status"].value
        by_status[st] = by_status.get(st, 0) + 1

    return {
        "shift_date": shift_date,
        "total_shifts": len(day_shifts),
        "by_status": by_status,
        "gates_covered": list({s["gate"] for s in day_shifts if s.get("gate")}),
        "flights_covered": list({s["flight_no"] for s in day_shifts if s.get("flight_no")}),
    }
