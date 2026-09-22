from typing import Optional, List
from pydantic import BaseModel, ConfigDict, Field


class ExpenseBase(BaseModel):
    id: str = Field(..., description="Unique transaction ID, e.g. exp-uuid")
    description: str = Field(..., description="Merchant or description")
    amount: float = Field(..., description="Signed transaction amount")
    creditCardId: str = Field(..., description="Referenced account or card ID")
    date: str = Field(..., description="ISO Date YYYY-MM-DD")
    fromTo: Optional[str] = Field(default=None, description="Payee / Payer for bank transactions")
    details: Optional[str] = Field(default=None, description="Notes, memo, or Zelle details")
    isFee: bool = Field(default=False, description="True if account or annual fee")
    isReward: bool = Field(default=False, description="True if reward / cashback credit")
    rewardType: Optional[str] = Field(default=None, description="cashback or other")
    rewardValue: Optional[float] = Field(default=None, description="Reward value in dollars or points")
    isTransfer: bool = Field(default=False, description="True if inter-account transfer")
    transferLinkId: Optional[str] = Field(default=None, description="Shared ID for linked transfer legs")
    isInterest: bool = Field(default=False, description="True if interest earned")
    category: str = Field(default="Others", description="Categorization label")
    username: str = Field(default="local", description="Username isolation key")
    isSyncDirty: bool = Field(default=False)
    updatedAt: Optional[int] = Field(default=None, description="Timestamp in milliseconds")

    model_config = ConfigDict(
        populate_by_name=True,
        from_attributes=True
    )


class ExpenseCreate(ExpenseBase):
    pass


class ExpenseUpdate(BaseModel):
    description: Optional[str] = None
    amount: Optional[float] = None
    creditCardId: Optional[str] = None
    date: Optional[str] = None
    fromTo: Optional[str] = None
    details: Optional[str] = None
    isFee: Optional[bool] = None
    isReward: Optional[bool] = None
    rewardType: Optional[str] = None
    rewardValue: Optional[float] = None
    isTransfer: Optional[bool] = None
    transferLinkId: Optional[str] = None
    isInterest: Optional[bool] = None
    category: Optional[str] = None
    isSyncDirty: Optional[bool] = None
    updatedAt: Optional[int] = None

    model_config = ConfigDict(
        populate_by_name=True,
        from_attributes=True
    )


class ExpenseResponse(ExpenseBase):
    pass


class ExpenseBatchRequest(BaseModel):
    expenses: List[ExpenseBase]
