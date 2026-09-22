#!/usr/bin/env python3
"""
Seed sample ExlDroid financial data into the database.
"""
import sys
import os
import asyncio
import time
from datetime import date

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

from app.database import AsyncSessionLocal, init_db
from app.models.card import CreditCardModel
from app.models.expense import ExpenseModel
from app.models.future_expense import FutureExpenseModel
from sqlalchemy import select


async def seed_data():
    await init_db()
    async with AsyncSessionLocal() as session:
        print("[*] Checking existing data...")
        res = await session.execute(select(CreditCardModel).where(CreditCardModel.username == "demo_user"))
        if res.scalars().first():
            print("[-] Demo data already exists for user 'demo_user'. Skipping.")
            return

        now_ms = int(time.time() * 1000)
        today = date.today().isoformat()

        # Seed Cards
        cards = [
            CreditCardModel(
                id="acc-chase-checking",
                name="Chase Total Checking",
                isChecking=True,
                isSaving=False,
                isBrokerage=False,
                isHidden=False,
                priority=0,
                openDate="2020-01-15",
                username="demo_user",
                isSyncDirty=False,
                updatedAt=now_ms
            ),
            CreditCardModel(
                id="card-csp",
                name="Chase Sapphire Preferred",
                isChecking=False,
                isSaving=False,
                isBrokerage=False,
                isHidden=False,
                priority=1,
                openDate="2021-06-10",
                username="demo_user",
                isSyncDirty=False,
                updatedAt=now_ms
            ),
            CreditCardModel(
                id="acc-ally-savings",
                name="Ally High-Yield Savings",
                isChecking=False,
                isSaving=True,
                isBrokerage=False,
                isHidden=False,
                priority=2,
                openDate="2022-03-01",
                username="demo_user",
                isSyncDirty=False,
                updatedAt=now_ms
            )
        ]
        for c in cards:
            session.add(c)

        # Seed Expenses
        expenses = [
            ExpenseModel(
                id="exp-rent-01",
                description="Apartment Rent",
                amount=-1850.00,
                creditCardId="acc-chase-checking",
                date=today,
                fromTo="Landlord",
                details="Monthly rent payment",
                isFee=False,
                isReward=False,
                isTransfer=False,
                isInterest=False,
                category="Rent",
                username="demo_user",
                isSyncDirty=False,
                updatedAt=now_ms
            ),
            ExpenseModel(
                id="exp-groceries-01",
                description="Trader Joe's",
                amount=124.50,
                creditCardId="card-csp",
                date=today,
                fromTo=None,
                details="Weekly groceries",
                isFee=False,
                isReward=False,
                isTransfer=False,
                isInterest=False,
                category="Groceries",
                username="demo_user",
                isSyncDirty=False,
                updatedAt=now_ms
            ),
            ExpenseModel(
                id="exp-dining-01",
                description="Ramen Bar",
                amount=45.20,
                creditCardId="card-csp",
                date=today,
                fromTo=None,
                details="Dinner with friends",
                isFee=False,
                isReward=False,
                isTransfer=False,
                isInterest=False,
                category="Food",
                username="demo_user",
                isSyncDirty=False,
                updatedAt=now_ms
            ),
            ExpenseModel(
                id="exp-interest-01",
                description="Monthly HYSA Interest",
                amount=85.30,
                creditCardId="acc-ally-savings",
                date=today,
                fromTo="Ally Bank",
                details="4.25% APY Interest",
                isFee=False,
                isReward=False,
                isTransfer=False,
                isInterest=True,
                category="Others",
                username="demo_user",
                isSyncDirty=False,
                updatedAt=now_ms
            ),
        ]
        for e in expenses:
            session.add(e)

        # Seed Future Expense
        future_bill = FutureExpenseModel(
            id="fut-car-ins-01",
            description="Geico Auto Insurance",
            amount=145.00,
            dueDate=today,
            username="demo_user",
            isSyncDirty=False,
            updatedAt=now_ms
        )
        session.add(future_bill)

        await session.commit()
        print("[+] Successfully seeded 3 cards, 4 expenses, and 1 scheduled bill for 'demo_user'!")


if __name__ == "__main__":
    asyncio.run(seed_data())
