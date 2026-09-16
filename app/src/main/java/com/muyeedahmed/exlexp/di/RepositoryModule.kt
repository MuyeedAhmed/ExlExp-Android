package com.muyeedahmed.exlexp.di

import com.muyeedahmed.exlexp.data.repository.CardRepositoryImpl
import com.muyeedahmed.exlexp.data.repository.ExpenseRepositoryImpl
import com.muyeedahmed.exlexp.data.repository.FutureExpenseRepositoryImpl
import com.muyeedahmed.exlexp.data.repository.SyncRepositoryImpl
import com.muyeedahmed.exlexp.domain.repository.CardRepository
import com.muyeedahmed.exlexp.domain.repository.ExpenseRepository
import com.muyeedahmed.exlexp.domain.repository.FutureExpenseRepository
import com.muyeedahmed.exlexp.domain.repository.SyncRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCardRepository(impl: CardRepositoryImpl): CardRepository

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(impl: ExpenseRepositoryImpl): ExpenseRepository

    @Binds
    @Singleton
    abstract fun bindFutureExpenseRepository(impl: FutureExpenseRepositoryImpl): FutureExpenseRepository

    @Binds
    @Singleton
    abstract fun bindSyncRepository(impl: SyncRepositoryImpl): SyncRepository
}
