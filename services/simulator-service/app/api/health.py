from fastapi import APIRouter

from app.config import get_settings

router = APIRouter(tags=["health"])


@router.get("/health")
def healthcheck() -> dict[str, str]:
    settings = get_settings()
    return {
        "service": settings.service_name,
        "status": "ok",
        "version": settings.service_version,
    }
