from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession

from app.database import get_db
from app.schemas.sync import SyncPushPayload, SyncResponse
from app.services.sync_service import SyncService
from app.services.auth_service import get_current_username_flexible

router = APIRouter()


@router.post("", response_model=SyncResponse, summary="Two-way Delta Synchronization")
async def sync_data(
    payload: SyncPushPayload,
    current_username: str = Depends(get_current_username_flexible),
    db: AsyncSession = Depends(get_db)
):
    """
    Primary two-way delta synchronization endpoint:
    1. Removes deleted records specified in deleted IDs and records tombstones.
    2. Upserts dirty cards, expenses, and future expenses sent by the mobile device.
    3. Retrieves any records modified on the server since `lastSyncTimestamp`.
    4. Returns updated records and tombstones for the client to apply locally.
    """
    effective_user = payload.username or current_username
    response = await SyncService.process_sync(db, payload, effective_user)
    return response
