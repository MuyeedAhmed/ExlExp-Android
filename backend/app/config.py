import os
from typing import List, Optional
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    # Application Info
    APP_NAME: str = "ExlDroid Backend API"
    APP_VERSION: str = "1.0.0"
    ENVIRONMENT: str = "development"
    DEBUG: bool = True
    PORT: int = 8000
    HOST: str = "0.0.0.0"
    API_V1_STR: str = "/api/v1"

    # Security & JWT
    SECRET_KEY: str = "exldroid_super_secret_jwt_key_2026_change_in_production"
    ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 60 * 24  # 24 hours

    # AWS Database (RDS / Aurora PostgreSQL) Parameters
    AWS_RDS_HOST: Optional[str] = None
    AWS_RDS_PORT: int = 5432
    AWS_RDS_DB_NAME: str = "exldroid"
    AWS_RDS_USER: str = "postgres"
    AWS_RDS_PASSWORD: Optional[str] = None
    AWS_RDS_SSL_MODE: str = "require"

    # Direct Database URL (takes precedence if provided)
    DATABASE_URL: Optional[str] = None

    # AWS General Configuration
    AWS_REGION: str = "us-east-1"
    AWS_ACCESS_KEY_ID: Optional[str] = None
    AWS_SECRET_ACCESS_KEY: Optional[str] = None
    AWS_SECRET_NAME: Optional[str] = None

    # CORS
    CORS_ORIGINS: str = "*"

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore"
    )

    @property
    def cors_origins_list(self) -> List[str]:
        if not self.CORS_ORIGINS or self.CORS_ORIGINS.strip() == "*":
            return ["*"]
        return [origin.strip() for origin in self.CORS_ORIGINS.split(",") if origin.strip()]

    @property
    def async_database_url(self) -> str:
        """
        Builds the appropriate async SQLAlchemy connection URL.
        1. If DATABASE_URL is set, normalizes PostgreSQL URLs to postgresql+asyncpg://
        2. Else if AWS_RDS_HOST is configured, constructs postgresql+asyncpg:// with SSL
        3. Defaults to local SQLite (sqlite+aiosqlite:///./exldroid.db)
        """
        if self.DATABASE_URL:
            url = self.DATABASE_URL.strip()
            # Normalize schema for asyncpg
            if url.startswith("postgres://"):
                url = "postgresql+asyncpg://" + url[len("postgres://"):]
            elif url.startswith("postgresql://") and not url.startswith("postgresql+asyncpg://"):
                url = "postgresql+asyncpg://" + url[len("postgresql://"):]
            return url

        if self.AWS_RDS_HOST:
            user = self.AWS_RDS_USER
            password = self.AWS_RDS_PASSWORD or ""
            host = self.AWS_RDS_HOST
            port = self.AWS_RDS_PORT
            db = self.AWS_RDS_DB_NAME
            ssl_param = f"?ssl={self.AWS_RDS_SSL_MODE}" if self.AWS_RDS_SSL_MODE else ""
            return f"postgresql+asyncpg://{user}:{password}@{host}:{port}/{db}{ssl_param}"

        # Default fallback: local async SQLite
        return "sqlite+aiosqlite:///./exldroid.db"

    @property
    def is_sqlite(self) -> bool:
        return self.async_database_url.startswith("sqlite")


settings = Settings()
