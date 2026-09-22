from typing import Optional, List
from pydantic import BaseModel, ConfigDict, Field


class FutureExpenseBase(BaseModel):
    id: str = Field(..., description="Unique bill ID, e.g. fut-uuid")
    description: str = Field(..., description="Bill or scheduled expense description")
    amount: float = Field(..., description="Estimated dollar amount")
    dueDate: Optional[str] = Field(default=None, description="Due date YYYY-MM-DD")
    username: str = Field(default="local", description="Username isolation key")
    isSyncDirty: bool = Field(default=False)
    updatedAt: Optional[int] = Field(default=None, description="Timestamp in milliseconds")

    model_config = ConfigDict(
        populate_by_name=True,
        from_attributes=True
    )


class FutureExpenseCreate(FutureExpenseBase):
    pass


class FutureExpenseUpdate(BaseModel):
    description: Optional[str] = None
    amount: Optional[float] = None
    dueDate: Optional[str] = None
    isSyncDirty: Optional[bool] = None
    updatedAt: Optional[int] = None

    model_config = ConfigDict(
        populate_by_name=True,
        from_attributes=True
    )


class FutureExpenseResponse(FutureExpenseBase):
    pass


class FutureExpenseBatchRequest(BaseModel):
    futureExpenses: List[FutureExpenseBase]
