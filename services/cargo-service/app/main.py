from fastapi import FastAPI
from prometheus_fastapi_instrumentator import Instrumentator

from app.api.cargo import router as cargo_router
from app.api.health import router as health_router
from app.config import get_settings

settings = get_settings()
app = FastAPI(
    title="AirOps360 Cargo Service",
    version=settings.service_version,
    description="Cargo unload workflow service for AirOps360.",
)
app.include_router(health_router)
app.include_router(cargo_router)

Instrumentator(excluded_handlers=["/health"]).instrument(app).expose(app, include_in_schema=False)
