from typing import List, Optional
from pydantic import BaseModel, ConfigDict, Field
from app.schemas.card import CardBase, CardResponse
from app.schemas.expense import ExpenseBase, ExpenseResponse
from app.schemas.future_expense import FutureExpenseBase, FutureExpenseResponse


class SyncPushPayload(BaseModel):
    username: Optional[str] = Field(default=None, description="Username (if not using JWT)")
    lastSyncTimestamp: Optional[int] = Field(default=None, description="Timestamp ms of last successful sync")
    deletedCardIds: List[str] = Field(default_factory=list)
    deletedExpenseIds: List[str] = Field(default_factory=list)
    deletedFutureExpenseIds: List[str] = Field(default_factory=list)
    cards: List[CardBase] = Field(default_factory=list)
    expenses: List[ExpenseBase] = Field(default_factory=list)
    futureExpenses: List[FutureExpenseBase] = Field(default_factory=list)

    model_config = ConfigDict(
        populate_by_name=True,
        from_attributes=True
    )


class SyncResponse(BaseModel):
    status: str = "success"
    serverTimestamp: int
    cards: List[CardResponse] = Field(default_factory=list)
    expenses: List[ExpenseResponse] = Field(default_factory=list)
    futureExpenses: List[FutureExpenseResponse] = Field(default_factory=list)
    deletedCardIds: List[str] = Field(default_factory=list)
    deletedExpenseIds: List[str] = Field(default_factory=list)
    deletedFutureExpenseIds: List[str] = Field(default_factory=list)
    message: str = "Sync completed successfully"

    model_config = ConfigDict(
        populate_by_name=True,
        from_attributes=True
    )
