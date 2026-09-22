from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy import select, or_
from sqlalchemy.ext.asyncio import AsyncSession

from app.database import get_db
from app.models.user import UserModel
from app.schemas.user import UserRegisterRequest, UserLoginRequest, TokenResponse, UserOut
from app.services.auth_service import (
    get_password_hash,
    verify_password,
    create_access_token,
    get_current_user,
)

router = APIRouter()


@router.post("/signup", response_model=UserOut, status_code=status.HTTP_201_CREATED, summary="Register user")
async def signup(user_in: UserRegisterRequest, db: AsyncSession = Depends(get_db)):
    # Check if username or email already exists
    stmt = select(UserModel).where(
        or_(UserModel.username == user_in.username, UserModel.email == user_in.email)
    )
    result = await db.execute(stmt)
    existing_user = result.scalar_one_or_none()
    if existing_user:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="A user with that username or email already exists"
        )

    user = UserModel(
        username=user_in.username,
        email=user_in.email,
        hashed_password=get_password_hash(user_in.password),
    )
    db.add(user)
    await db.commit()
    await db.refresh(user)
    return user


@router.post("/login", response_model=TokenResponse, summary="User login & obtain JWT token")
async def login(credentials: UserLoginRequest, db: AsyncSession = Depends(get_db)):
    stmt = select(UserModel).where(
        or_(
            UserModel.username == credentials.username_or_email,
            UserModel.email == credentials.username_or_email
        )
    )
    result = await db.execute(stmt)
    user = result.scalar_one_or_none()
    if not user or not verify_password(credentials.password, user.hashed_password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect username/email or password",
            headers={"WWW-Authenticate": "Bearer"},
        )

    access_token = create_access_token(data={"sub": user.username})
    return TokenResponse(
        access_token=access_token,
        token_type="bearer",
        username=user.username,
        email=user.email
    )


@router.get("/me", response_model=UserOut, summary="Get current authenticated user")
async def get_me(current_user: UserModel = Depends(get_current_user)):
    return current_user
