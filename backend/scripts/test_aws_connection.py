#!/usr/bin/env python3
"""
AWS Database Connectivity Test Utility for ExlDroid Backend.
Validates DNS, TCP socket reachability, SSL handshake, and DB authentication.
"""
import sys
import os
import socket
import asyncio

# Ensure app is importable from backend directory
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

from app.config import settings
from sqlalchemy import text
from sqlalchemy.ext.asyncio import create_async_engine


async def test_connection():
    print("=" * 70)
    print(" ExlDroid Backend: AWS Database Connectivity Test")
    print("=" * 70)

    db_url = settings.async_database_url
    print(f"Target Database URL: {db_url.split('@')[-1] if '@' in db_url else db_url}")
    print(f"Engine Type:         {'Local SQLite' if settings.is_sqlite else 'AWS PostgreSQL / RDS'}")
    print(f"Configured Host:     {settings.AWS_RDS_HOST or '(None - using fallback)'}")
    print(f"Configured Port:     {settings.AWS_RDS_PORT}")
    print(f"Configured Database: {settings.AWS_RDS_DB_NAME}")
    print(f"Configured User:     {settings.AWS_RDS_USER}")
    print("-" * 70)

    if settings.AWS_RDS_HOST:
        print(f"[*] Step 1: Testing TCP Socket to {settings.AWS_RDS_HOST}:{settings.AWS_RDS_PORT}...")
        try:
            sock = socket.create_connection(
                (settings.AWS_RDS_HOST, settings.AWS_RDS_PORT),
                timeout=5.0
            )
            sock.close()
            print("    [SUCCESS] TCP port is open and reachable from this machine.")
        except socket.timeout:
            print("    [ERROR] Connection timed out! Check:")
            print("            1. AWS RDS Security Group inbound rule: Port 5432 must allow your IP.")
            print("            2. AWS RDS 'Publicly Accessible' setting must be 'Yes'.")
            return False
        except socket.gaierror as e:
            print(f"    [ERROR] DNS resolution failed for host {settings.AWS_RDS_HOST}: {e}")
            return False
        except Exception as e:
            print(f"    [ERROR] Socket error: {e}")
            return False

    print("[*] Step 2: Testing Async Database Engine Handshake...")
    try:
        connect_args = {"check_same_thread": False} if settings.is_sqlite else {}
        test_engine = create_async_engine(db_url, connect_args=connect_args, echo=False)
        async with test_engine.connect() as conn:
            result = await conn.execute(text("SELECT 1"))
            val = result.scalar()
            if val == 1:
                print("    [SUCCESS] Executed test query (SELECT 1). Database responds!")

            try:
                ver_res = await conn.execute(text("SELECT version()"))
                ver = ver_res.scalar()
                print(f"    [INFO] Database Engine: {ver}")
            except Exception:
                pass
        await test_engine.dispose()
        print("-" * 70)
        print("[SUCCESS] All database connection tests passed!")
        print("=" * 70)
        return True
    except Exception as e:
        print("    [ERROR] Database connection failed:")
        print(f"            {type(e).__name__}: {e}")
        print("\nCommon Troubleshooting Tips:")
        print("1. If 'password authentication failed': Check AWS_RDS_USER and AWS_RDS_PASSWORD in .env")
        print("2. If 'database does not exist': Create database or check AWS_RDS_DB_NAME in .env")
        print("3. If SSL error: Verify AWS_RDS_SSL_MODE=require in .env")
        print("=" * 70)
        return False


if __name__ == "__main__":
    success = asyncio.run(test_connection())
    sys.exit(0 if success else 1)
