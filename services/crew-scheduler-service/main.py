from fastapi import FastAPI, HTTPException, Depends, status
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager
import logging
import uvicorn

from app.routers import crew, shifts, health
from app.kafka.producer import KafkaProducer
from app.config.settings import settings

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info("Starting Crew Scheduler Service...")
    yield
    logger.info("Shutting down Crew Scheduler Service...")


app = FastAPI(
    title="AirOps360 Crew Scheduler Service",
    description="Ground crew scheduling, shift management, and real-time crew alerts",
    version="1.0.0",
    lifespan=lifespan,
    docs_url="/swagger-ui.html",
    openapi_url="/api-docs",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(health.router, prefix="/health", tags=["Health"])
app.include_router(crew.router, prefix="/api/v1/crew", tags=["Crew"])
app.include_router(shifts.router, prefix="/api/v1/shifts", tags=["Shifts"])

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8083, reload=True)
