import pytest
from httpx import AsyncClient


@pytest.mark.asyncio
async def test_health_check(client: AsyncClient):
    response = await client.get("/api/v1/health")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "online"
    assert "database" in data
    assert data["database"]["connected"] is True


@pytest.mark.asyncio
async def test_auth_workflow(client: AsyncClient):
    # 1. Signup
    signup_payload = {
        "username": "tester",
        "email": "tester@example.com",
        "password": "securepassword123"
    }
    signup_res = await client.post("/api/v1/auth/signup", json=signup_payload)
    assert signup_res.status_code == 201
    user_data = signup_res.json()
    assert user_data["username"] == "tester"
    assert user_data["email"] == "tester@example.com"

    # Duplicate signup should fail
    dup_res = await client.post("/api/v1/auth/signup", json=signup_payload)
    assert dup_res.status_code == 400

    # 2. Login
    login_payload = {
        "username_or_email": "tester",
        "password": "securepassword123"
    }
    login_res = await client.post("/api/v1/auth/login", json=login_payload)
    assert login_res.status_code == 200
    token_data = login_res.json()
    assert "access_token" in token_data
    token = token_data["access_token"]

    # 3. Authenticated endpoint
    me_res = await client.get("/api/v1/auth/me", headers={"Authorization": f"Bearer {token}"})
    assert me_res.status_code == 200
    assert me_res.json()["username"] == "tester"


@pytest.mark.asyncio
async def test_cards_crud(client: AsyncClient):
    # Create card
    card_payload = {
        "id": "card-test-01",
        "name": "Test Visa Signature",
        "isChecking": False,
        "isSaving": False,
        "isBrokerage": False,
        "isHidden": False,
        "priority": 0,
        "openDate": "2024-01-01",
        "username": "tester",
        "isSyncDirty": False
    }
    create_res = await client.post("/api/v1/cards", json=card_payload)
    assert create_res.status_code == 201
    assert create_res.json()["name"] == "Test Visa Signature"

    # Get cards
    get_res = await client.get("/api/v1/cards?username=tester")
    assert get_res.status_code == 200
    cards = get_res.json()
    assert len(cards) >= 1
    assert any(c["id"] == "card-test-01" for c in cards)

    # Update card
    update_res = await client.put("/api/v1/cards/card-test-01", json={"name": "Updated Visa"})
    assert update_res.status_code == 200
    assert update_res.json()["name"] == "Updated Visa"

    # Delete card
    del_res = await client.delete("/api/v1/cards/card-test-01")
    assert del_res.status_code == 204


@pytest.mark.asyncio
async def test_expenses_crud_and_filters(client: AsyncClient):
    # Create card first
    await client.post("/api/v1/cards", json={
        "id": "card-for-exp",
        "name": "Checking Account",
        "isChecking": True,
        "openDate": "2024-01-01",
        "username": "tester"
    })

    # Create expense
    exp_payload = {
        "id": "exp-test-01",
        "description": "Starbucks Coffee",
        "amount": 6.75,
        "creditCardId": "card-for-exp",
        "date": "2026-09-20",
        "fromTo": "Starbucks",
        "details": "Morning latte",
        "category": "Food",
        "username": "tester"
    }
    create_res = await client.post("/api/v1/expenses", json=exp_payload)
    assert create_res.status_code == 201
    assert create_res.json()["amount"] == 6.75

    # Filter by category
    filter_res = await client.get("/api/v1/expenses?username=tester&category=Food")
    assert filter_res.status_code == 200
    assert len(filter_res.json()) >= 1

    # Search filter
    search_res = await client.get("/api/v1/expenses?username=tester&search=latte")
    assert search_res.status_code == 200
    assert len(search_res.json()) >= 1

    # Delete expense
    del_res = await client.delete("/api/v1/expenses/exp-test-01")
    assert del_res.status_code == 204


@pytest.mark.asyncio
async def test_future_expenses(client: AsyncClient):
    fut_payload = {
        "id": "fut-test-01",
        "description": "Electric Utility Bill",
        "amount": 95.0,
        "dueDate": "2026-10-01",
        "username": "tester"
    }
    create_res = await client.post("/api/v1/future-expenses", json=fut_payload)
    assert create_res.status_code == 201

    get_res = await client.get("/api/v1/future-expenses?username=tester")
    assert get_res.status_code == 200
    assert len(get_res.json()) >= 1

    del_res = await client.delete("/api/v1/future-expenses/fut-test-01")
    assert del_res.status_code == 204


@pytest.mark.asyncio
async def test_two_way_sync(client: AsyncClient):
    sync_payload = {
        "username": "sync_user",
        "lastSyncTimestamp": 0,
        "cards": [
            {
                "id": "sync-card-01",
                "name": "Sync Bank",
                "isChecking": True,
                "openDate": "2025-01-01",
                "username": "sync_user"
            }
        ],
        "expenses": [
            {
                "id": "sync-exp-01",
                "description": "Sync Lunch",
                "amount": 15.50,
                "creditCardId": "sync-card-01",
                "date": "2026-09-21",
                "category": "Food",
                "username": "sync_user"
            }
        ],
        "futureExpenses": [],
        "deletedCardIds": [],
        "deletedExpenseIds": [],
        "deletedFutureExpenseIds": []
    }

    res = await client.post("/api/v1/sync", json=sync_payload)
    assert res.status_code == 200
    sync_data = res.json()
    assert sync_data["status"] == "success"
    assert sync_data["serverTimestamp"] > 0

    # Verify stored in database
    cards_res = await client.get("/api/v1/cards?username=sync_user")
    assert len(cards_res.json()) == 1
    assert cards_res.json()[0]["id"] == "sync-card-01"


@pytest.mark.asyncio
async def test_backup_export_and_import(client: AsyncClient):
    # Import full dataset
    import_payload = {
        "exportDate": "2026-09-22T00:00:00",
        "username": "backup_user",
        "cards": [
            {
                "id": "backup-card-01",
                "name": "Backup Card",
                "isChecking": False,
                "openDate": "2024-05-01",
                "username": "backup_user"
            }
        ],
        "expenses": [
            {
                "id": "backup-exp-01",
                "description": "Backup Flight Ticket",
                "amount": 420.00,
                "creditCardId": "backup-card-01",
                "date": "2026-09-15",
                "category": "Travel",
                "username": "backup_user"
            }
        ],
        "futureExpenses": []
    }

    import_res = await client.post("/api/v1/backup/import", json=import_payload)
    assert import_res.status_code == 200
    res_json = import_res.json()
    assert res_json["cardsCount"] == 1
    assert res_json["expensesCount"] == 1

    # Export backup
    export_res = await client.get("/api/v1/backup/export?username=backup_user")
    assert export_res.status_code == 200
    export_json = export_res.json()
    assert export_json["username"] == "backup_user"
    assert len(export_json["cards"]) == 1
    assert len(export_json["expenses"]) == 1
