from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession

from app.database import get_db
from app.services.aws_service import AWSService
from app.config import settings

router = APIRouter()


@router.get("/health", summary="Health check & Database diagnostic")
async def health_check(db: AsyncSession = Depends(get_db)):
    """Verifies service responsiveness and connectivity to the AWS database."""
    db_health = await AWSService.check_database_health(db)
    return {
        "status": "online",
        "app_name": settings.APP_NAME,
        "version": settings.APP_VERSION,
        "environment": settings.ENVIRONMENT,
        "database": db_health
    }
