import time
from datetime import datetime
from typing import Optional
from fastapi import APIRouter, Depends, Query, HTTPException
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.database import get_db
from app.models.card import CreditCardModel
from app.models.expense import ExpenseModel
from app.models.future_expense import FutureExpenseModel
from app.schemas.backup import BackupPayload, ImportResult
from app.schemas.card import CardBase
from app.schemas.expense import ExpenseBase
from app.schemas.future_expense import FutureExpenseBase
from app.services.auth_service import get_current_username_flexible

router = APIRouter()


@router.get("/export", response_model=BackupPayload, summary="Export entire user financial dataset")
async def export_backup(
    username: Optional[str] = Query(None, description="Target username"),
    current_username: str = Depends(get_current_username_flexible),
    db: AsyncSession = Depends(get_db)
):
    target_user = username or current_username

    cards_res = await db.execute(select(CreditCardModel).where(CreditCardModel.username == target_user))
    expenses_res = await db.execute(select(ExpenseModel).where(ExpenseModel.username == target_user))
    future_res = await db.execute(select(FutureExpenseModel).where(FutureExpenseModel.username == target_user))

    cards = [CardBase.model_validate(c) for c in cards_res.scalars().all()]
    expenses = [ExpenseBase.model_validate(e) for e in expenses_res.scalars().all()]
    future_expenses = [FutureExpenseBase.model_validate(f) for f in future_res.scalars().all()]

    return BackupPayload(
        exportDate=datetime.utcnow().isoformat(),
        username=target_user,
        cards=cards,
        expenses=expenses,
        futureExpenses=future_expenses
    )


@router.post("/import", response_model=ImportResult, summary="Import financial dataset directly into database")
async def import_backup(
    backup: BackupPayload,
    current_username: str = Depends(get_current_username_flexible),
    db: AsyncSession = Depends(get_db)
):
    target_user = backup.username if backup.username != "local" else current_username
    now_ms = int(time.time() * 1000)

    cards_count = 0
    expenses_count = 0
    future_count = 0

    # 1. Upsert cards
    for card_in in backup.cards:
        stmt = select(CreditCardModel).where(CreditCardModel.id == card_in.id)
        res = await db.execute(stmt)
        existing = res.scalar_one_or_none()
        c_dict = card_in.model_dump()
        c_dict["username"] = target_user
        c_dict["updatedAt"] = card_in.updatedAt or now_ms

        if existing:
            for k, v in c_dict.items():
                setattr(existing, k, v)
        else:
            db.add(CreditCardModel(**c_dict))
        cards_count += 1

    # 2. Upsert expenses
    for exp_in in backup.expenses:
        stmt = select(ExpenseModel).where(ExpenseModel.id == exp_in.id)
        res = await db.execute(stmt)
        existing = res.scalar_one_or_none()
        e_dict = exp_in.model_dump()
        e_dict["username"] = target_user
        e_dict["updatedAt"] = exp_in.updatedAt or now_ms

        if existing:
            for k, v in e_dict.items():
                setattr(existing, k, v)
        else:
            db.add(ExpenseModel(**e_dict))
        expenses_count += 1

    # 3. Upsert future expenses
    for fut_in in backup.futureExpenses:
        stmt = select(FutureExpenseModel).where(FutureExpenseModel.id == fut_in.id)
        res = await db.execute(stmt)
        existing = res.scalar_one_or_none()
        f_dict = fut_in.model_dump()
        f_dict["username"] = target_user
        f_dict["updatedAt"] = fut_in.updatedAt or now_ms

        if existing:
            for k, v in f_dict.items():
                setattr(existing, k, v)
        else:
            db.add(FutureExpenseModel(**f_dict))
        future_count += 1

    await db.commit()

    return ImportResult(
        cardsCount=cards_count,
        expensesCount=expenses_count,
        futureExpensesCount=future_count,
        message="Backup successfully imported into the AWS database."
    )
