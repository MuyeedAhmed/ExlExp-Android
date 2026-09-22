import time
from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy import select, delete
from sqlalchemy.ext.asyncio import AsyncSession

from app.database import get_db
from app.models.card import CreditCardModel
from app.models.deleted_record import DeletedRecordModel
from app.schemas.card import CardBase, CardCreate, CardUpdate, CardResponse, CardBatchRequest
from app.services.auth_service import get_current_username_flexible

router = APIRouter()


@router.get("", response_model=List[CardResponse], summary="List all cards/accounts for user")
async def get_cards(
    username: Optional[str] = Query(None, description="Username filter (if unauthenticated)"),
    current_username: str = Depends(get_current_username_flexible),
    db: AsyncSession = Depends(get_db)
):
    target_user = username or current_username
    stmt = select(CreditCardModel).where(CreditCardModel.username == target_user).order_by(CreditCardModel.priority.asc(), CreditCardModel.name.asc())
    result = await db.execute(stmt)
    return result.scalars().all()


@router.post("", response_model=CardResponse, status_code=status.HTTP_201_CREATED, summary="Create or upsert card")
async def create_or_upsert_card(
    card_in: CardCreate,
    current_username: str = Depends(get_current_username_flexible),
    db: AsyncSession = Depends(get_db)
):
    target_user = card_in.username if card_in.username != "local" else current_username
    stmt = select(CreditCardModel).where(CreditCardModel.id == card_in.id)
    result = await db.execute(stmt)
    existing = result.scalar_one_or_none()

    now_ms = int(time.time() * 1000)
    card_data = card_in.model_dump()
    card_data["username"] = target_user
    card_data["updatedAt"] = card_in.updatedAt or now_ms

    if existing:
        for key, val in card_data.items():
            setattr(existing, key, val)
        await db.commit()
        await db.refresh(existing)
        return existing
    else:
        new_card = CreditCardModel(**card_data)
        db.add(new_card)
        await db.commit()
        await db.refresh(new_card)
        return new_card


@router.get("/{card_id}", response_model=CardResponse, summary="Get card by ID")
async def get_card(
    card_id: str,
    current_username: str = Depends(get_current_username_flexible),
    db: AsyncSession = Depends(get_db)
):
    stmt = select(CreditCardModel).where(CreditCardModel.id == card_id)
    result = await db.execute(stmt)
    card = result.scalar_one_or_none()
    if not card:
        raise HTTPException(status_code=404, detail="Card not found")
    return card


@router.put("/{card_id}", response_model=CardResponse, summary="Update card")
async def update_card(
    card_id: str,
    card_in: CardUpdate,
    db: AsyncSession = Depends(get_db)
):
    stmt = select(CreditCardModel).where(CreditCardModel.id == card_id)
    result = await db.execute(stmt)
    card = result.scalar_one_or_none()
    if not card:
        raise HTTPException(status_code=404, detail="Card not found")

    update_data = card_in.model_dump(exclude_unset=True)
    update_data["updatedAt"] = int(time.time() * 1000)
    for key, val in update_data.items():
        setattr(card, key, val)

    await db.commit()
    await db.refresh(card)
    return card


@router.delete("/{card_id}", status_code=status.HTTP_204_NO_CONTENT, summary="Delete card")
async def delete_card(
    card_id: str,
    current_username: str = Depends(get_current_username_flexible),
    db: AsyncSession = Depends(get_db)
):
    stmt = select(CreditCardModel).where(CreditCardModel.id == card_id)
    result = await db.execute(stmt)
    card = result.scalar_one_or_none()
    if card:
        now_ms = int(time.time() * 1000)
        # Record tombstone
        db.add(DeletedRecordModel(id=card.id, tableName="cards", username=card.username, deletedAt=now_ms))
        await db.delete(card)
        await db.commit()
    return None


@router.post("/batch", response_model=List[CardResponse], summary="Batch upsert cards")
async def batch_upsert_cards(
    batch: CardBatchRequest,
    current_username: str = Depends(get_current_username_flexible),
    db: AsyncSession = Depends(get_db)
):
    results = []
    now_ms = int(time.time() * 1000)
    for card_in in batch.cards:
        target_user = card_in.username if card_in.username != "local" else current_username
        stmt = select(CreditCardModel).where(CreditCardModel.id == card_in.id)
        res = await db.execute(stmt)
        existing = res.scalar_one_or_none()

        card_data = card_in.model_dump()
        card_data["username"] = target_user
        card_data["updatedAt"] = card_in.updatedAt or now_ms

        if existing:
            for key, val in card_data.items():
                setattr(existing, key, val)
            results.append(existing)
        else:
            new_card = CreditCardModel(**card_data)
            db.add(new_card)
            results.append(new_card)

    await db.commit()
    for item in results:
        await db.refresh(item)
    return results
