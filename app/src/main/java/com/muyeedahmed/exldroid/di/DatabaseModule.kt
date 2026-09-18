package com.muyeedahmed.exldroid.di

import android.content.Context
import com.muyeedahmed.exldroid.data.local.AppDatabase
import com.muyeedahmed.exldroid.data.local.dao.CardDao
import com.muyeedahmed.exldroid.data.local.dao.DeletedRecordDao
import com.muyeedahmed.exldroid.data.local.dao.ExpenseDao
import com.muyeedahmed.exldroid.data.local.dao.FutureExpenseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideCardDao(database: AppDatabase): CardDao {
        return database.cardDao()
    }

    @Provides
    fun provideExpenseDao(database: AppDatabase): ExpenseDao {
        return database.expenseDao()
    }

    @Provides
    fun provideFutureExpenseDao(database: AppDatabase): FutureExpenseDao {
        return database.futureExpenseDao()
    }

    @Provides
    fun provideDeletedRecordDao(database: AppDatabase): DeletedRecordDao {
        return database.deletedRecordDao()
    }
}
