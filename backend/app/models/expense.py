import time
from sqlalchemy import Column, String, Boolean, Float, BigInteger, Index, Text
from app.database import Base


class ExpenseModel(Base):
    __tablename__ = "expenses"

    id = Column(String(100), primary_key=True, index=True)
    description = Column(String(500), nullable=False)
    amount = Column(Float, nullable=False)
    creditCardId = Column(String(100), nullable=False, index=True)
    date = Column(String(50), nullable=False, index=True)
    fromTo = Column(String(200), nullable=True)
    details = Column(Text, nullable=True)
    isFee = Column(Boolean, default=False, nullable=False)
    isReward = Column(Boolean, default=False, nullable=False)
    rewardType = Column(String(50), nullable=True)
    rewardValue = Column(Float, nullable=True)
    isTransfer = Column(Boolean, default=False, nullable=False)
    transferLinkId = Column(String(100), nullable=True, index=True)
    isInterest = Column(Boolean, default=False, nullable=False)
    category = Column(String(100), default="Others", index=True, nullable=False)
    username = Column(String(100), default="local", index=True, nullable=False)
    isSyncDirty = Column(Boolean, default=False, nullable=False)
    updatedAt = Column(BigInteger, default=lambda: int(time.time() * 1000), onupdate=lambda: int(time.time() * 1000), nullable=False)

    __table_args__ = (
        Index("idx_expenses_user_date", "username", "date"),
        Index("idx_expenses_user_card", "username", "creditCardId"),
        Index("idx_expenses_user_cat", "username", "category"),
    )
