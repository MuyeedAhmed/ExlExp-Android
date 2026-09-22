import time
from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy import select, delete
from sqlalchemy.ext.asyncio import AsyncSession

from app.database import get_db
from app.models.future_expense import FutureExpenseModel
from app.models.deleted_record import DeletedRecordModel
from app.schemas.future_expense import (
    FutureExpenseBase,
    FutureExpenseCreate,
    FutureExpenseUpdate,
    FutureExpenseResponse,
    FutureExpenseBatchRequest,
)
from app.services.auth_service import get_current_username_flexible

router = APIRouter()


@router.get("", response_model=List[FutureExpenseResponse], summary="List future expenses / scheduled bills")
async def get_future_expenses(
    username: Optional[str] = Query(None, description="Username filter"),
    current_username: str = Depends(get_current_username_flexible),
    db: AsyncSession = Depends(get_db)
):
    target_user = username or current_username
    stmt = select(FutureExpenseModel).where(FutureExpenseModel.username == target_user).order_by(FutureExpenseModel.dueDate.asc())
    result = await db.execute(stmt)
    return result.scalars().all()


@router.post("", response_model=FutureExpenseResponse, status_code=status.HTTP_201_CREATED, summary="Create or upsert future expense")
async def create_or_upsert_future_expense(
    fut_in: FutureExpenseCreate,
    current_username: str = Depends(get_current_username_flexible),
    db: AsyncSession = Depends(get_db)
):
    target_user = fut_in.username if fut_in.username != "local" else current_username
    stmt = select(FutureExpenseModel).where(FutureExpenseModel.id == fut_in.id)
    result = await db.execute(stmt)
    existing = result.scalar_one_or_none()

    now_ms = int(time.time() * 1000)
    fut_data = fut_in.model_dump()
    fut_data["username"] = target_user
    fut_data["updatedAt"] = fut_in.updatedAt or now_ms

    if existing:
        for key, val in fut_data.items():
            setattr(existing, key, val)
        await db.commit()
        await db.refresh(existing)
        return existing
    else:
        new_fut = FutureExpenseModel(**fut_data)
        db.add(new_fut)
        await db.commit()
        await db.refresh(new_fut)
        return new_fut


@router.get("/{fut_id}", response_model=FutureExpenseResponse, summary="Get future expense by ID")
async def get_future_expense(
    fut_id: str,
    db: AsyncSession = Depends(get_db)
):
    stmt = select(FutureExpenseModel).where(FutureExpenseModel.id == fut_id)
    result = await db.execute(stmt)
    fut = result.scalar_one_or_none()
    if not fut:
        raise HTTPException(status_code=404, detail="Future expense not found")
    return fut


@router.put("/{fut_id}", response_model=FutureExpenseResponse, summary="Update future expense")
async def update_future_expense(
    fut_id: str,
    fut_in: FutureExpenseUpdate,
    db: AsyncSession = Depends(get_db)
):
    stmt = select(FutureExpenseModel).where(FutureExpenseModel.id == fut_id)
    result = await db.execute(stmt)
    fut = result.scalar_one_or_none()
    if not fut:
        raise HTTPException(status_code=404, detail="Future expense not found")

    update_data = fut_in.model_dump(exclude_unset=True)
    update_data["updatedAt"] = int(time.time() * 1000)
    for key, val in update_data.items():
        setattr(fut, key, val)

    await db.commit()
    await db.refresh(fut)
    return fut


@router.delete("/{fut_id}", status_code=status.HTTP_204_NO_CONTENT, summary="Delete future expense")
async def delete_future_expense(
    fut_id: str,
    db: AsyncSession = Depends(get_db)
):
    stmt = select(FutureExpenseModel).where(FutureExpenseModel.id == fut_id)
    result = await db.execute(stmt)
    fut = result.scalar_one_or_none()
    if fut:
        now_ms = int(time.time() * 1000)
        # Record tombstone
        db.add(DeletedRecordModel(id=fut.id, tableName="future_expenses", username=fut.username, deletedAt=now_ms))
        await db.delete(fut)
        await db.commit()
    return None


@router.post("/batch", response_model=List[FutureExpenseResponse], summary="Batch upsert future expenses")
async def batch_upsert_future_expenses(
    batch: FutureExpenseBatchRequest,
    current_username: str = Depends(get_current_username_flexible),
    db: AsyncSession = Depends(get_db)
):
    results = []
    now_ms = int(time.time() * 1000)
    for fut_in in batch.futureExpenses:
        target_user = fut_in.username if fut_in.username != "local" else current_username
        stmt = select(FutureExpenseModel).where(FutureExpenseModel.id == fut_in.id)
        res = await db.execute(stmt)
        existing = res.scalar_one_or_none()

        fut_data = fut_in.model_dump()
        fut_data["username"] = target_user
        fut_data["updatedAt"] = fut_in.updatedAt or now_ms

        if existing:
            for key, val in fut_data.items():
                setattr(existing, key, val)
            results.append(existing)
        else:
            new_fut = FutureExpenseModel(**fut_data)
            db.add(new_fut)
            results.append(new_fut)

    await db.commit()
    for item in results:
        await db.refresh(item)
    return results
