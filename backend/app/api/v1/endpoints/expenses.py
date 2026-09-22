import time
from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy import select, delete, or_
from sqlalchemy.ext.asyncio import AsyncSession

from app.database import get_db
from app.models.expense import ExpenseModel
from app.models.deleted_record import DeletedRecordModel
from app.schemas.expense import (
    ExpenseBase,
    ExpenseCreate,
    ExpenseUpdate,
    ExpenseResponse,
    ExpenseBatchRequest,
)
from app.services.auth_service import get_current_username_flexible

router = APIRouter()


@router.get("", response_model=List[ExpenseResponse], summary="Query expenses with filters")
async def get_expenses(
    username: Optional[str] = Query(None, description="Username filter"),
    start_date: Optional[str] = Query(None, description="Start date YYYY-MM-DD"),
    end_date: Optional[str] = Query(None, description="End date YYYY-MM-DD"),
    category: Optional[str] = Query(None, description="Category filter"),
    credit_card_id: Optional[str] = Query(None, description="Card or Account ID filter"),
    search: Optional[str] = Query(None, description="Text search in description, payee, notes"),
    limit: int = Query(100, ge=1, le=1000),
    offset: int = Query(0, ge=0),
    current_username: str = Depends(get_current_username_flexible),
    db: AsyncSession = Depends(get_db)
):
    target_user = username or current_username
    stmt = select(ExpenseModel).where(ExpenseModel.username == target_user)

    if start_date:
        stmt = stmt.where(ExpenseModel.date >= start_date)
    if end_date:
        stmt = stmt.where(ExpenseModel.date <= end_date)
    if category:
        stmt = stmt.where(ExpenseModel.category == category)
    if credit_card_id:
        stmt = stmt.where(ExpenseModel.creditCardId == credit_card_id)
    if search:
        pattern = f"%{search}%"
        stmt = stmt.where(
            or_(
                ExpenseModel.description.ilike(pattern),
                ExpenseModel.fromTo.ilike(pattern),
                ExpenseModel.details.ilike(pattern)
            )
        )

    stmt = stmt.order_by(ExpenseModel.date.desc(), ExpenseModel.updatedAt.desc()).offset(offset).limit(limit)
    result = await db.execute(stmt)
    return result.scalars().all()


@router.post("", response_model=ExpenseResponse, status_code=status.HTTP_201_CREATED, summary="Create or upsert expense")
async def create_or_upsert_expense(
    exp_in: ExpenseCreate,
    current_username: str = Depends(get_current_username_flexible),
    db: AsyncSession = Depends(get_db)
):
    target_user = exp_in.username if exp_in.username != "local" else current_username
    stmt = select(ExpenseModel).where(ExpenseModel.id == exp_in.id)
    result = await db.execute(stmt)
    existing = result.scalar_one_or_none()

    now_ms = int(time.time() * 1000)
    exp_data = exp_in.model_dump()
    exp_data["username"] = target_user
    exp_data["updatedAt"] = exp_in.updatedAt or now_ms

    if existing:
        for key, val in exp_data.items():
            setattr(existing, key, val)
        await db.commit()
        await db.refresh(existing)
        return existing
    else:
        new_exp = ExpenseModel(**exp_data)
        db.add(new_exp)
        await db.commit()
        await db.refresh(new_exp)
        return new_exp


@router.get("/{expense_id}", response_model=ExpenseResponse, summary="Get expense by ID")
async def get_expense(
    expense_id: str,
    db: AsyncSession = Depends(get_db)
):
    stmt = select(ExpenseModel).where(ExpenseModel.id == expense_id)
    result = await db.execute(stmt)
    exp = result.scalar_one_or_none()
    if not exp:
        raise HTTPException(status_code=404, detail="Expense not found")
    return exp


@router.put("/{expense_id}", response_model=ExpenseResponse, summary="Update expense")
async def update_expense(
    expense_id: str,
    exp_in: ExpenseUpdate,
    db: AsyncSession = Depends(get_db)
):
    stmt = select(ExpenseModel).where(ExpenseModel.id == expense_id)
    result = await db.execute(stmt)
    exp = result.scalar_one_or_none()
    if not exp:
        raise HTTPException(status_code=404, detail="Expense not found")

    update_data = exp_in.model_dump(exclude_unset=True)
    update_data["updatedAt"] = int(time.time() * 1000)
    for key, val in update_data.items():
        setattr(exp, key, val)

    await db.commit()
    await db.refresh(exp)
    return exp


@router.delete("/{expense_id}", status_code=status.HTTP_204_NO_CONTENT, summary="Delete expense")
async def delete_expense(
    expense_id: str,
    db: AsyncSession = Depends(get_db)
):
    stmt = select(ExpenseModel).where(ExpenseModel.id == expense_id)
    result = await db.execute(stmt)
    exp = result.scalar_one_or_none()
    if exp:
        now_ms = int(time.time() * 1000)
        # Record tombstone
        db.add(DeletedRecordModel(id=exp.id, tableName="expenses", username=exp.username, deletedAt=now_ms))
        await db.delete(exp)
        await db.commit()
    return None


@router.post("/batch", response_model=List[ExpenseResponse], summary="Batch upsert expenses")
async def batch_upsert_expenses(
    batch: ExpenseBatchRequest,
    current_username: str = Depends(get_current_username_flexible),
    db: AsyncSession = Depends(get_db)
):
    results = []
    now_ms = int(time.time() * 1000)
    for exp_in in batch.expenses:
        target_user = exp_in.username if exp_in.username != "local" else current_username
        stmt = select(ExpenseModel).where(ExpenseModel.id == exp_in.id)
        res = await db.execute(stmt)
        existing = res.scalar_one_or_none()

        exp_data = exp_in.model_dump()
        exp_data["username"] = target_user
        exp_data["updatedAt"] = exp_in.updatedAt or now_ms

        if existing:
            for key, val in exp_data.items():
                setattr(existing, key, val)
            results.append(existing)
        else:
            new_exp = ExpenseModel(**exp_data)
            db.add(new_exp)
            results.append(new_exp)

    await db.commit()
    for item in results:
        await db.refresh(item)
    return results
