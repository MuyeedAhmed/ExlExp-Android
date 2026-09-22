from fastapi import APIRouter
from app.api.v1.endpoints import (
    health,
    auth,
    cards,
    expenses,
    future_expenses,
    sync,
    backup,
)

api_router = APIRouter()

api_router.include_router(health.router, tags=["Health"])
api_router.include_router(auth.router, prefix="/auth", tags=["Authentication"])
api_router.include_router(cards.router, prefix="/cards", tags=["Cards & Accounts"])
api_router.include_router(expenses.router, prefix="/expenses", tags=["Expenses & Transactions"])
api_router.include_router(future_expenses.router, prefix="/future-expenses", tags=["Future Bills"])
api_router.include_router(sync.router, prefix="/sync", tags=["Delta Synchronization"])
api_router.include_router(backup.router, prefix="/backup", tags=["Backup & Restore"])
