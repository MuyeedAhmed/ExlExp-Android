import time
from sqlalchemy import Column, String, Boolean, Float, BigInteger, Index
from app.database import Base


class FutureExpenseModel(Base):
    __tablename__ = "future_expenses"

    id = Column(String(100), primary_key=True, index=True)
    description = Column(String(500), nullable=False)
    amount = Column(Float, nullable=False)
    dueDate = Column(String(50), nullable=True)
    username = Column(String(100), default="local", index=True, nullable=False)
    isSyncDirty = Column(Boolean, default=False, nullable=False)
    updatedAt = Column(BigInteger, default=lambda: int(time.time() * 1000), onupdate=lambda: int(time.time() * 1000), nullable=False)

    __table_args__ = (
        Index("idx_future_expenses_user", "username"),
    )
