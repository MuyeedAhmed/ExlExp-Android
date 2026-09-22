-- =============================================================================
-- ExlDroid AWS RDS PostgreSQL Database Schema Initialization Script
-- Run this script in AWS RDS Query Editor, psql, pgAdmin, or DBeaver.
-- =============================================================================

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    hashed_password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT (NOW() AT TIME ZONE 'utc') NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- 2. Cards / Accounts Table
CREATE TABLE IF NOT EXISTS cards (
    id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    "isChecking" BOOLEAN DEFAULT FALSE NOT NULL,
    "isSaving" BOOLEAN DEFAULT FALSE NOT NULL,
    "isBrokerage" BOOLEAN DEFAULT FALSE NOT NULL,
    "isHidden" BOOLEAN DEFAULT FALSE NOT NULL,
    priority INTEGER DEFAULT 0 NOT NULL,
    "openDate" VARCHAR(50) NOT NULL,
    username VARCHAR(100) DEFAULT 'local' NOT NULL,
    "isSyncDirty" BOOLEAN DEFAULT FALSE NOT NULL,
    "updatedAt" BIGINT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_cards_username ON cards(username);
CREATE INDEX IF NOT EXISTS idx_cards_username_priority ON cards(username, priority);

-- 3. Expenses / Transactions Table
CREATE TABLE IF NOT EXISTS expenses (
    id VARCHAR(100) PRIMARY KEY,
    description VARCHAR(500) NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    "creditCardId" VARCHAR(100) NOT NULL,
    date VARCHAR(50) NOT NULL,
    "fromTo" VARCHAR(200),
    details TEXT,
    "isFee" BOOLEAN DEFAULT FALSE NOT NULL,
    "isReward" BOOLEAN DEFAULT FALSE NOT NULL,
    "rewardType" VARCHAR(50),
    "rewardValue" DOUBLE PRECISION,
    "isTransfer" BOOLEAN DEFAULT FALSE NOT NULL,
    "transferLinkId" VARCHAR(100),
    "isInterest" BOOLEAN DEFAULT FALSE NOT NULL,
    category VARCHAR(100) DEFAULT 'Others' NOT NULL,
    username VARCHAR(100) DEFAULT 'local' NOT NULL,
    "isSyncDirty" BOOLEAN DEFAULT FALSE NOT NULL,
    "updatedAt" BIGINT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_expenses_username ON expenses(username);
CREATE INDEX IF NOT EXISTS idx_expenses_user_date ON expenses(username, date);
CREATE INDEX IF NOT EXISTS idx_expenses_user_card ON expenses(username, "creditCardId");
CREATE INDEX IF NOT EXISTS idx_expenses_user_cat ON expenses(username, category);
CREATE INDEX IF NOT EXISTS idx_expenses_transfer_link ON expenses("transferLinkId");

-- 4. Future Expenses / Scheduled Bills Table
CREATE TABLE IF NOT EXISTS future_expenses (
    id VARCHAR(100) PRIMARY KEY,
    description VARCHAR(500) NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    "dueDate" VARCHAR(50),
    username VARCHAR(100) DEFAULT 'local' NOT NULL,
    "isSyncDirty" BOOLEAN DEFAULT FALSE NOT NULL,
    "updatedAt" BIGINT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_future_expenses_user ON future_expenses(username);

-- 5. Deleted Records / Tombstones Table
CREATE TABLE IF NOT EXISTS deleted_records (
    id VARCHAR(100) PRIMARY KEY,
    "tableName" VARCHAR(50) NOT NULL,
    "deletedAt" BIGINT NOT NULL,
    username VARCHAR(100) DEFAULT 'local' NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_deleted_records_user_table ON deleted_records(username, "tableName");
CREATE INDEX IF NOT EXISTS idx_deleted_records_deleted_at ON deleted_records("deletedAt");
