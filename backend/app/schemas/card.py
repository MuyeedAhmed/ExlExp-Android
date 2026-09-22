from typing import Optional, List
from pydantic import BaseModel, ConfigDict, Field


class CardBase(BaseModel):
    id: str = Field(..., description="Card unique identifier, e.g. card-chase")
    name: str = Field(..., description="Account or Card name")
    isChecking: bool = Field(default=False, description="True if checking account")
    isSaving: bool = Field(default=False, description="True if savings account")
    isBrokerage: bool = Field(default=False, description="True if investment/brokerage")
    isHidden: bool = Field(default=False, description="True if hidden from UI")
    priority: int = Field(default=0, description="Display order priority")
    openDate: str = Field(..., description="Opening date in YYYY-MM-DD")
    username: str = Field(default="local", description="Username isolation key")
    isSyncDirty: bool = Field(default=False)
    updatedAt: Optional[int] = Field(default=None, description="Timestamp in milliseconds")

    model_config = ConfigDict(
        populate_by_name=True,
        from_attributes=True
    )


class CardCreate(CardBase):
    pass


class CardUpdate(BaseModel):
    name: Optional[str] = None
    isChecking: Optional[bool] = None
    isSaving: Optional[bool] = None
    isBrokerage: Optional[bool] = None
    isHidden: Optional[bool] = None
    priority: Optional[int] = None
    openDate: Optional[str] = None
    isSyncDirty: Optional[bool] = None
    updatedAt: Optional[int] = None

    model_config = ConfigDict(
        populate_by_name=True,
        from_attributes=True
    )


class CardResponse(CardBase):
    pass


class CardBatchRequest(BaseModel):
    cards: List[CardBase]
