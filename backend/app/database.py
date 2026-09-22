import logging
from typing import AsyncGenerator
from sqlalchemy.ext.asyncio import (
    AsyncSession,
    async_sessionmaker,
    create_async_engine,
)
from sqlalchemy.orm import declarative_base
from app.config import settings

logger = logging.getLogger(__name__)

# Engine configuration kwargs
engine_kwargs = {
    "echo": settings.DEBUG and not settings.is_sqlite,
    "future": True,
}

if settings.is_sqlite:
    # SQLite does not support connection pooling the same way
    engine_kwargs["connect_args"] = {"check_same_thread": False}
else:
    # AWS RDS PostgreSQL connection pool configuration
    engine_kwargs["pool_size"] = 10
    engine_kwargs["max_overflow"] = 20
    engine_kwargs["pool_pre_ping"] = True  # Handles RDS timeouts/drops gracefully
    engine_kwargs["pool_recycle"] = 1800   # Recycle connections every 30 minutes

engine = create_async_engine(settings.async_database_url, **engine_kwargs)

AsyncSessionLocal = async_sessionmaker(
    bind=engine,
    class_=AsyncSession,
    expire_on_commit=False,
    autocommit=False,
    autoflush=False,
)

Base = declarative_base()


async def get_db() -> AsyncGenerator[AsyncSession, None]:
    """Dependency that yields an async database session per request."""
    async with AsyncSessionLocal() as session:
        try:
            yield session
        except Exception:
            await session.rollback()
            raise
        finally:
            await session.close()


async def init_db() -> None:
    """Creates database tables if they do not exist."""
    # Import all models to ensure they are registered on Base.metadata
    from app.models.card import CreditCardModel
    from app.models.expense import ExpenseModel
    from app.models.future_expense import FutureExpenseModel
    from app.models.deleted_record import DeletedRecordModel
    from app.models.user import UserModel

    logger.info("Initializing database schema on: %s", settings.async_database_url.split("@")[-1])
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)
    logger.info("Database schema initialized successfully.")
