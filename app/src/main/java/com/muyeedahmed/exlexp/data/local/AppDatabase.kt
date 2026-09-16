package com.muyeedahmed.exlexp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.muyeedahmed.exlexp.data.local.dao.CardDao
import com.muyeedahmed.exlexp.data.local.dao.DeletedRecordDao
import com.muyeedahmed.exlexp.data.local.dao.ExpenseDao
import com.muyeedahmed.exlexp.data.local.dao.FutureExpenseDao
import com.muyeedahmed.exlexp.data.local.entity.CreditCardEntity
import com.muyeedahmed.exlexp.data.local.entity.DeletedRecordEntity
import com.muyeedahmed.exlexp.data.local.entity.ExpenseEntity
import com.muyeedahmed.exlexp.data.local.entity.FutureExpenseEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

@Database(
    entities = [
        CreditCardEntity::class,
        ExpenseEntity::class,
        FutureExpenseEntity::class,
        DeletedRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cardDao(): CardDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun futureExpenseDao(): FutureExpenseDao
    abstract fun deletedRecordDao(): DeletedRecordDao

    companion object {
        const val DATABASE_NAME = "exlexp_database.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .setJournalMode(JournalMode.WRITE_AHEAD_LOGGING)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Prepopulate default guest/local accounts
                            CoroutineScope(Dispatchers.IO).launch {
                                getInstance(context).prepopulateDefaults()
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun prepopulateDefaults() {
        if (cardDao().getCardCount() == 0) {
            val today = LocalDate.now().toString()
            val defaultAccounts = listOf(
                CreditCardEntity(
                    id = "acc-checking-default",
                    name = "Cash / Checking",
                    isChecking = true,
                    isSaving = false,
                    isBrokerage = false,
                    priority = 0,
                    openDate = today,
                    username = "local"
                ),
                CreditCardEntity(
                    id = "card-credit-default",
                    name = "Primary Credit Card",
                    isChecking = false,
                    isSaving = false,
                    isBrokerage = false,
                    priority = 1,
                    openDate = today,
                    username = "local"
                )
            )
            cardDao().insertCards(defaultAccounts)
        }
    }
}
