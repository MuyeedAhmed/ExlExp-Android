import time
from sqlalchemy import Column, String, Boolean, Integer, BigInteger, Index
from app.database import Base


class CreditCardModel(Base):
    __tablename__ = "cards"

    id = Column(String(100), primary_key=True, index=True)
    name = Column(String(200), nullable=False)
    isChecking = Column(Boolean, default=False, nullable=False)
    isSaving = Column(Boolean, default=False, nullable=False)
    isBrokerage = Column(Boolean, default=False, nullable=False)
    isHidden = Column(Boolean, default=False, nullable=False)
    priority = Column(Integer, default=0, nullable=False)
    openDate = Column(String(50), nullable=False)
    username = Column(String(100), default="local", index=True, nullable=False)
    isSyncDirty = Column(Boolean, default=False, nullable=False)
    updatedAt = Column(BigInteger, default=lambda: int(time.time() * 1000), onupdate=lambda: int(time.time() * 1000), nullable=False)

    __table_args__ = (
        Index("idx_cards_username_priority", "username", "priority"),
    )
