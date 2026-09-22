import time
from sqlalchemy import Column, String, BigInteger, Index
from app.database import Base


class DeletedRecordModel(Base):
    __tablename__ = "deleted_records"

    id = Column(String(100), primary_key=True, index=True)
    tableName = Column(String(50), nullable=False)
    deletedAt = Column(BigInteger, default=lambda: int(time.time() * 1000), nullable=False)
    username = Column(String(100), default="local", index=True, nullable=False)

    __table_args__ = (
        Index("idx_deleted_records_user_table", "username", "tableName"),
    )
