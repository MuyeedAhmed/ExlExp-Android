import json
import logging
from typing import Dict, Any, Optional
from sqlalchemy import text
from sqlalchemy.ext.asyncio import AsyncSession
from app.config import settings

logger = logging.getLogger(__name__)


class AWSService:
    @staticmethod
    async def check_database_health(db: AsyncSession) -> Dict[str, Any]:
        """
        Executes a diagnostic query against the active database
        (AWS RDS PostgreSQL, MySQL, or local test DB).
        """
        try:
            result = await db.execute(text("SELECT 1"))
            val = result.scalar()
            is_healthy = val == 1

            # Determine dialect
            bind = db.bind
            dialect_name = bind.dialect.name if bind else "unknown"

            db_host = settings.AWS_RDS_HOST or (
                "sqlite" if settings.is_sqlite else settings.async_database_url.split("@")[-1]
            )

            return {
                "status": "healthy" if is_healthy else "unhealthy",
                "dialect": dialect_name,
                "host": db_host,
                "ssl_mode": settings.AWS_RDS_SSL_MODE if not settings.is_sqlite else "none",
                "connected": is_healthy
            }
        except Exception as e:
            logger.error("Database health check failed: %s", str(e))
            return {
                "status": "unhealthy",
                "error": str(e),
                "connected": False
            }

    @staticmethod
    def get_aws_secret(secret_name: str, region_name: Optional[str] = None) -> Optional[Dict[str, str]]:
        """
        Retrieves credentials from AWS Secrets Manager using boto3.
        Only executed if boto3 and AWS credentials/role are available.
        """
        try:
            import boto3
            from botocore.exceptions import ClientError

            region = region_name or settings.AWS_REGION
            session = boto3.session.Session()
            client = session.client(
                service_name="secretsmanager",
                region_name=region
            )

            response = client.get_secret_value(SecretId=secret_name)
            if "SecretString" in response:
                return json.loads(response["SecretString"])
            return None
        except Exception as e:
            logger.warning("Could not fetch secret %s from AWS Secrets Manager: %s", secret_name, str(e))
            return None
