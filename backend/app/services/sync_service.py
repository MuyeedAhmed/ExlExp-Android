import time
import logging
from typing import List, Set
from sqlalchemy import select, delete
from sqlalchemy.ext.asyncio import AsyncSession

from app.models.card import CreditCardModel
from app.models.expense import ExpenseModel
from app.models.future_expense import FutureExpenseModel
from app.models.deleted_record import DeletedRecordModel
from app.schemas.sync import SyncPushPayload, SyncResponse
from app.schemas.card import CardResponse
from app.schemas.expense import ExpenseResponse
from app.schemas.future_expense import FutureExpenseResponse

logger = logging.getLogger(__name__)


class SyncService:
    @staticmethod
    async def process_sync(
        db: AsyncSession,
        payload: SyncPushPayload,
        effective_username: str
    ) -> SyncResponse:
        now_ms = int(time.time() * 1000)
        pushed_card_ids: Set[str] = set()
        pushed_expense_ids: Set[str] = set()
        pushed_future_ids: Set[str] = set()

        # 1. Process deletions from client
        if payload.deletedCardIds:
            for card_id in payload.deletedCardIds:
                await db.execute(delete(CreditCardModel).where(CreditCardModel.id == card_id, CreditCardModel.username == effective_username))
                db.add(DeletedRecordModel(id=card_id, tableName="cards", username=effective_username, deletedAt=now_ms))

        if payload.deletedExpenseIds:
            for exp_id in payload.deletedExpenseIds:
                await db.execute(delete(ExpenseModel).where(ExpenseModel.id == exp_id, ExpenseModel.username == effective_username))
                db.add(DeletedRecordModel(id=exp_id, tableName="expenses", username=effective_username, deletedAt=now_ms))

        if payload.deletedFutureExpenseIds:
            for fut_id in payload.deletedFutureExpenseIds:
                await db.execute(delete(FutureExpenseModel).where(FutureExpenseModel.id == fut_id, FutureExpenseModel.username == effective_username))
                db.add(DeletedRecordModel(id=fut_id, tableName="future_expenses", username=effective_username, deletedAt=now_ms))

        # 2. Upsert cards
        for card_data in payload.cards:
            pushed_card_ids.add(card_data.id)
            stmt = select(CreditCardModel).where(CreditCardModel.id == card_data.id)
            res = await db.execute(stmt)
            existing = res.scalar_one_or_none()

            card_dict = card_data.model_dump(exclude={"isSyncDirty"})
            card_dict["username"] = effective_username
            card_dict["isSyncDirty"] = False
            card_dict["updatedAt"] = card_data.updatedAt or now_ms

            if existing:
                for key, val in card_dict.items():
                    setattr(existing, key, val)
            else:
                db.add(CreditCardModel(**card_dict))

        # 3. Upsert expenses
        for exp_data in payload.expenses:
            pushed_expense_ids.add(exp_data.id)
            stmt = select(ExpenseModel).where(ExpenseModel.id == exp_data.id)
            res = await db.execute(stmt)
            existing = res.scalar_one_or_none()

            exp_dict = exp_data.model_dump(exclude={"isSyncDirty"})
            exp_dict["username"] = effective_username
            exp_dict["isSyncDirty"] = False
            exp_dict["updatedAt"] = exp_data.updatedAt or now_ms

            if existing:
                for key, val in exp_dict.items():
                    setattr(existing, key, val)
            else:
                db.add(ExpenseModel(**exp_dict))

        # 4. Upsert future expenses
        for fut_data in payload.futureExpenses:
            pushed_future_ids.add(fut_data.id)
            stmt = select(FutureExpenseModel).where(FutureExpenseModel.id == fut_data.id)
            res = await db.execute(stmt)
            existing = res.scalar_one_or_none()

            fut_dict = fut_data.model_dump(exclude={"isSyncDirty"})
            fut_dict["username"] = effective_username
            fut_dict["isSyncDirty"] = False
            fut_dict["updatedAt"] = fut_data.updatedAt or now_ms

            if existing:
                for key, val in fut_dict.items():
                    setattr(existing, key, val)
            else:
                db.add(FutureExpenseModel(**fut_dict))

        # Commit push changes
        await db.commit()

        # 5. Query delta updates for the client
        last_ts = payload.lastSyncTimestamp or 0

        # Query updated cards
        card_query = select(CreditCardModel).where(CreditCardModel.username == effective_username)
        if last_ts > 0:
            card_query = card_query.where(CreditCardModel.updatedAt > last_ts)
        card_res = await db.execute(card_query)
        updated_cards = [
            CardResponse.model_validate(c)
            for c in card_res.scalars().all()
            if c.id not in pushed_card_ids
        ]

        # Query updated expenses
        exp_query = select(ExpenseModel).where(ExpenseModel.username == effective_username)
        if last_ts > 0:
            exp_query = exp_query.where(ExpenseModel.updatedAt > last_ts)
        exp_res = await db.execute(exp_query)
        updated_expenses = [
            ExpenseResponse.model_validate(e)
            for e in exp_res.scalars().all()
            if e.id not in pushed_expense_ids
        ]

        # Query updated future expenses
        fut_query = select(FutureExpenseModel).where(FutureExpenseModel.username == effective_username)
        if last_ts > 0:
            fut_query = fut_query.where(FutureExpenseModel.updatedAt > last_ts)
        fut_res = await db.execute(fut_query)
        updated_future = [
            FutureExpenseResponse.model_validate(f)
            for f in fut_res.scalars().all()
            if f.id not in pushed_future_ids
        ]

        # Query server deletions
        del_card_ids: List[str] = []
        del_exp_ids: List[str] = []
        del_fut_ids: List[str] = []

        if last_ts > 0:
            del_query = select(DeletedRecordModel).where(
                DeletedRecordModel.username == effective_username,
                DeletedRecordModel.deletedAt > last_ts
            )
            del_res = await db.execute(del_query)
            for record in del_res.scalars().all():
                if record.tableName == "cards" and record.id not in payload.deletedCardIds:
                    del_card_ids.append(record.id)
                elif record.tableName == "expenses" and record.id not in payload.deletedExpenseIds:
                    del_exp_ids.append(record.id)
                elif record.tableName == "future_expenses" and record.id not in payload.deletedFutureExpenseIds:
                    del_fut_ids.append(record.id)

        return SyncResponse(
            status="success",
            serverTimestamp=now_ms,
            cards=updated_cards,
            expenses=updated_expenses,
            futureExpenses=updated_future,
            deletedCardIds=del_card_ids,
            deletedExpenseIds=del_exp_ids,
            deletedFutureExpenseIds=del_fut_ids,
            message="Synchronization completed successfully."
        )
