from functools import lru_cache

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    service_name: str = "cargo-service"
    service_version: str = "0.0.1"
    mongo_database: str = "airops360"
    mongo_collection: str = "cargo_metadata"

    model_config = SettingsConfigDict(env_prefix="CARGO_", case_sensitive=False)


@lru_cache
def get_settings() -> Settings:
    return Settings()
