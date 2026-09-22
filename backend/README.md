# ExlDroid Python Backend (AWS Database Integration)

A high-performance, asynchronous REST API backend built with **FastAPI**, **SQLAlchemy 2.0**, and **Pydantic v2** to store, query, and synchronize financial data (accounts, cards, transactions, and scheduled bills) to and from an **AWS database** (AWS RDS / Aurora PostgreSQL).

---

## Features

- **AWS Database Support**: Native connection pooling, SSL/TLS (`sslmode=require`), and schema management for **AWS RDS PostgreSQL** and **AWS Aurora**.
- **Local Fallback**: Instant local execution with **SQLite (`aiosqlite`)** when AWS parameters are not configured — zero setup required for local development.
- **Two-Way Delta Synchronization (`/api/v1/sync`)**: Mirrors the mobile app's offline-first architecture with tombstone deletion tracking and conflict-free delta sync.
- **Full CRUD & Batch Processing**: Endpoints for cards/accounts, expenses/transactions, and scheduled bills with batch upsert capabilities.
- **Backup Export & Import**: Native compatibility with the ExlDroid JSON backup contract (`BackupPayload`).
- **Interactive Documentation**: Auto-generated Swagger UI (`/docs`) and ReDoc (`/redoc`).
- **Cloud & Container Ready**: Includes multi-stage `Dockerfile`, `docker-compose.yml`, and `Mangum` handler for AWS Lambda.

---

## Directory Structure

```
backend/
├── app/
│   ├── api/v1/
│   │   ├── endpoints/
│   │   │   ├── auth.py             # User signup, login, JWT
│   │   │   ├── backup.py           # JSON export & import
│   │   │   ├── cards.py            # Accounts & Credit cards CRUD
│   │   │   ├── expenses.py         # Transactions CRUD & filtering
│   │   │   ├── future_expenses.py  # Scheduled bills CRUD
│   │   │   ├── health.py           # Health check & DB diagnostics
│   │   │   └── sync.py             # Two-way delta synchronization
│   │   └── router.py               # API v1 router bundling
│   ├── models/                     # SQLAlchemy 2.0 ORM models
│   ├── schemas/                    # Pydantic v2 schemas
│   ├── services/                   # Business logic (sync, auth, AWS)
│   ├── config.py                   # Pydantic settings & AWS config
│   ├── database.py                 # Async SQLAlchemy engine & session
│   └── main.py                     # FastAPI entrypoint & lifespan
├── scripts/
│   ├── init_aws_rds.sql            # AWS RDS PostgreSQL DDL script
│   ├── test_aws_connection.py      # Connection & SSL diagnostic CLI
│   └── seed_mock_data.py           # Sample data seeder
├── tests/                          # Pytest test suite
├── Dockerfile                      # Multi-stage container image
├── docker-compose.yml              # Local app + PostgreSQL stack
├── requirements.txt                # Production dependencies
├── .env.example                    # Environment template
└── README.md
```

---

## Quickstart

### 1. Local Setup (Zero Dependencies / SQLite Fallback)

```bash
cd backend
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt

# Start the development server
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```
Open **http://localhost:8000/docs** in your browser to interact with the Swagger API documentation.

---

### 2. Connecting to an AWS RDS PostgreSQL Database

#### Step A: Provision AWS RDS PostgreSQL
1. In the AWS Management Console, navigate to **RDS** -> **Databases** -> **Create database**.
2. Select **PostgreSQL** (version 15 or 16).
3. Set your DB instance identifier (e.g. `exldroid-db`), Master username (`postgres`), and a strong Master password.
4. **Connectivity**:
   - Set **Public access** to **Yes** (if connecting directly from outside the VPC).
   - In **VPC security group**, add an inbound rule:
     - Type: `PostgreSQL` (Port `5432`)
     - Source: `My IP` (or `0.0.0.0/0` if accessed from cloud services, or specific security group).
5. Initial database name: Set to `exldroid` under Additional configuration.

#### Step B: Configure `.env`
Edit `backend/.env` with your AWS RDS credentials:

```ini
AWS_RDS_HOST=exldroid-db.cxxxxxx.us-east-1.rds.amazonaws.com
AWS_RDS_PORT=5432
AWS_RDS_DB_NAME=exldroid
AWS_RDS_USER=postgres
AWS_RDS_PASSWORD=YourSecureMasterPassword
AWS_RDS_SSL_MODE=require
```

#### Step C: Validate AWS Connection
Run the included connection verification utility:

```bash
python scripts/test_aws_connection.py
```

This will test:
1. DNS resolution of your RDS endpoint.
2. TCP socket connection on port 5432 (security group verification).
3. SSL handshake with RDS.
4. Database authentication and query execution (`SELECT 1`).

#### Step D: Initialize Schema
Tables will automatically be created on server startup via SQLAlchemy `init_db()`.
Alternatively, you can run `scripts/init_aws_rds.sql` directly in the AWS RDS Query Editor or pgAdmin.

---

### 3. Running with Docker Compose

To test with a local PostgreSQL container simulating AWS RDS:

```bash
docker compose up -d --build
```
The API is accessible at `http://localhost:8000/docs` and PostgreSQL on port `5432`.

---

## API Endpoints Reference

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/health` | Service health & AWS DB connection status |
| `POST` | `/api/v1/auth/signup` | Register new user |
| `POST` | `/api/v1/auth/login` | Login and receive JWT access token |
| `GET` | `/api/v1/auth/me` | Current authenticated user profile |
| `GET` | `/api/v1/cards` | List cards and bank accounts |
| `POST` | `/api/v1/cards` | Create or upsert a card |
| `POST` | `/api/v1/cards/batch` | Batch upsert cards |
| `DELETE` | `/api/v1/cards/{id}` | Delete card & record tombstone |
| `GET` | `/api/v1/expenses` | Query transactions (date range, category, search) |
| `POST` | `/api/v1/expenses` | Create or upsert a transaction |
| `POST` | `/api/v1/expenses/batch`| Batch upsert transactions |
| `DELETE` | `/api/v1/expenses/{id}`| Delete transaction & record tombstone |
| `GET` | `/api/v1/future-expenses` | List scheduled bills |
| `POST` | `/api/v1/future-expenses`| Create or upsert scheduled bill |
| `POST` | `/api/v1/sync` | **Two-way delta sync** (push dirty records + pull server updates) |
| `GET` | `/api/v1/backup/export` | Export entire dataset as JSON backup |
| `POST` | `/api/v1/backup/import` | Import full JSON backup into AWS database |

---

## Running Automated Tests

```bash
cd backend
python -m pytest tests/ -v
```
All tests run against an isolated in-memory async SQLite engine with 100% test independence.
