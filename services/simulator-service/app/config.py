from functools import lru_cache

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    service_name: str = "simulator-service"
    service_version: str = "0.0.1"
    default_airline: str = "AA"
    default_origin_airport: str = "DFW"
    default_destination_airport: str = "ORD"

    model_config = SettingsConfigDict(env_prefix="SIMULATOR_", case_sensitive=False)


@lru_cache
def get_settings() -> Settings:
    return Settings()
