from typing import List, Optional
from pydantic import BaseModel, ConfigDict, Field
from app.schemas.card import CardBase
from app.schemas.expense import ExpenseBase
from app.schemas.future_expense import FutureExpenseBase


class BackupPayload(BaseModel):
    exportDate: str
    username: str
    cards: List[CardBase] = Field(default_factory=list)
    expenses: List[ExpenseBase] = Field(default_factory=list)
    futureExpenses: List[FutureExpenseBase] = Field(default_factory=list)

    model_config = ConfigDict(
        populate_by_name=True,
        from_attributes=True
    )


class ImportResult(BaseModel):
    cardsCount: int
    expensesCount: int
    futureExpensesCount: int
    message: str = "Backup imported successfully"
