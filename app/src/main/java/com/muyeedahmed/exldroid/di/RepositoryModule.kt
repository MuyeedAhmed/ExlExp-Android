package com.muyeedahmed.exldroid.di

import com.muyeedahmed.exldroid.data.repository.CardRepositoryImpl
import com.muyeedahmed.exldroid.data.repository.ExpenseRepositoryImpl
import com.muyeedahmed.exldroid.data.repository.FutureExpenseRepositoryImpl
import com.muyeedahmed.exldroid.data.repository.SyncRepositoryImpl
import com.muyeedahmed.exldroid.domain.repository.CardRepository
import com.muyeedahmed.exldroid.domain.repository.ExpenseRepository
import com.muyeedahmed.exldroid.domain.repository.FutureExpenseRepository
import com.muyeedahmed.exldroid.domain.repository.SyncRepository
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
