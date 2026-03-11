from fastapi import FastAPI
from prometheus_fastapi_instrumentator import Instrumentator

from app.api.health import router as health_router
from app.api.simulator import router as simulator_router
from app.config import get_settings

settings = get_settings()
app = FastAPI(
    title="AirOps360 Simulator Service",
    version=settings.service_version,
    description="Synthetic event generator for AirOps360 operational flows.",
)
app.include_router(health_router)
app.include_router(simulator_router)

Instrumentator(excluded_handlers=["/health"]).instrument(app).expose(app, include_in_schema=False)
