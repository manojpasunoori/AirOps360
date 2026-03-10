from fastapi import APIRouter, HTTPException, status
from pydantic import BaseModel, Field
from typing import Optional
from uuid import UUID, uuid4
from datetime import datetime
from enum import Enum

router = APIRouter()

# ── Models ────────────────────────────────────────────────────────

class StaffStatus(str, Enum):
    ACTIVE = "ACTIVE"
    ON_LEAVE = "ON_LEAVE"
    INACTIVE = "INACTIVE"

class StaffCreate(BaseModel):
    employee_id: str = Field(..., min_length=3)
    full_name: str = Field(..., min_length=2)
    role: str
    certification: Optional[str] = None

class StaffResponse(BaseModel):
    id: UUID
    employee_id: str
    full_name: str
    role: str
    certification: Optional[str]
    status: StaffStatus
    created_at: datetime

# In-memory store — swap with SQLAlchemy + PostgreSQL
_staff_db: dict[str, dict] = {
    "EMP001": {"id": uuid4(), "employee_id": "EMP001", "full_name": "James Carter",
               "role": "Ground Handler", "certification": "GH-CERT-A",
               "status": StaffStatus.ACTIVE, "created_at": datetime.utcnow()},
    "EMP002": {"id": uuid4(), "employee_id": "EMP002", "full_name": "Sarah Mitchell",
               "role": "Baggage Handler", "certification": "BH-CERT-B",
               "status": StaffStatus.ACTIVE, "created_at": datetime.utcnow()},
    "EMP004": {"id": uuid4(), "employee_id": "EMP004", "full_name": "Lisa Torres",
               "role": "Crew Supervisor", "certification": "SUP-CERT-A",
               "status": StaffStatus.ACTIVE, "created_at": datetime.utcnow()},
}

# ── Endpoints ─────────────────────────────────────────────────────

@router.get("/", response_model=list[StaffResponse])
async def get_all_crew():
    """Get all crew members"""
    return list(_staff_db.values())

@router.get("/{employee_id}", response_model=StaffResponse)
async def get_crew_member(employee_id: str):
    """Get crew member by employee ID"""
    staff = _staff_db.get(employee_id)
    if not staff:
        raise HTTPException(status_code=404, detail=f"Employee {employee_id} not found")
    return staff

@router.post("/", response_model=StaffResponse, status_code=status.HTTP_201_CREATED)
async def create_crew_member(staff: StaffCreate):
    """Register a new crew member"""
    if staff.employee_id in _staff_db:
        raise HTTPException(status_code=409, detail=f"Employee {staff.employee_id} already exists")

    new_staff = {
        "id": uuid4(),
        "employee_id": staff.employee_id,
        "full_name": staff.full_name,
        "role": staff.role,
        "certification": staff.certification,
        "status": StaffStatus.ACTIVE,
        "created_at": datetime.utcnow(),
    }
    _staff_db[staff.employee_id] = new_staff
    return new_staff

@router.patch("/{employee_id}/status")
async def update_crew_status(employee_id: str, new_status: StaffStatus):
    """Update crew member status"""
    staff = _staff_db.get(employee_id)
    if not staff:
        raise HTTPException(status_code=404, detail=f"Employee {employee_id} not found")
    staff["status"] = new_status
    return {"employee_id": employee_id, "status": new_status, "updated_at": datetime.utcnow()}

@router.get("/available/{role}")
async def get_available_crew_by_role(role: str):
    """Get available crew members for a specific role"""
    available = [
        s for s in _staff_db.values()
        if s["role"].lower() == role.lower() and s["status"] == StaffStatus.ACTIVE
    ]
    return {"role": role, "available_count": len(available), "crew": available}
