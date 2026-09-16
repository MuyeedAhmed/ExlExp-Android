package com.muyeedahmed.exlexp.data.repository;

import android.content.Context;
import com.muyeedahmed.exlexp.data.local.dao.CardDao;
import com.muyeedahmed.exlexp.data.local.dao.DeletedRecordDao;
import com.muyeedahmed.exlexp.data.local.dao.ExpenseDao;
import com.muyeedahmed.exlexp.data.local.dao.FutureExpenseDao;
import com.muyeedahmed.exlexp.data.remote.SupabaseClientProvider;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class SyncRepositoryImpl_Factory implements Factory<SyncRepositoryImpl> {
  private final Provider<Context> contextProvider;

  private final Provider<SupabaseClientProvider> supabaseProvider;

  private final Provider<CardDao> cardDaoProvider;

  private final Provider<ExpenseDao> expenseDaoProvider;

  private final Provider<FutureExpenseDao> futureExpenseDaoProvider;

  private final Provider<DeletedRecordDao> deletedRecordDaoProvider;

  public SyncRepositoryImpl_Factory(Provider<Context> contextProvider,
      Provider<SupabaseClientProvider> supabaseProvider, Provider<CardDao> cardDaoProvider,
      Provider<ExpenseDao> expenseDaoProvider, Provider<FutureExpenseDao> futureExpenseDaoProvider,
      Provider<DeletedRecordDao> deletedRecordDaoProvider) {
    this.contextProvider = contextProvider;
    this.supabaseProvider = supabaseProvider;
    this.cardDaoProvider = cardDaoProvider;
    this.expenseDaoProvider = expenseDaoProvider;
    this.futureExpenseDaoProvider = futureExpenseDaoProvider;
    this.deletedRecordDaoProvider = deletedRecordDaoProvider;
  }

  @Override
  public SyncRepositoryImpl get() {
    return newInstance(contextProvider.get(), supabaseProvider.get(), cardDaoProvider.get(), expenseDaoProvider.get(), futureExpenseDaoProvider.get(), deletedRecordDaoProvider.get());
  }

  public static SyncRepositoryImpl_Factory create(Provider<Context> contextProvider,
      Provider<SupabaseClientProvider> supabaseProvider, Provider<CardDao> cardDaoProvider,
      Provider<ExpenseDao> expenseDaoProvider, Provider<FutureExpenseDao> futureExpenseDaoProvider,
      Provider<DeletedRecordDao> deletedRecordDaoProvider) {
    return new SyncRepositoryImpl_Factory(contextProvider, supabaseProvider, cardDaoProvider, expenseDaoProvider, futureExpenseDaoProvider, deletedRecordDaoProvider);
  }

  public static SyncRepositoryImpl newInstance(Context context,
      SupabaseClientProvider supabaseProvider, CardDao cardDao, ExpenseDao expenseDao,
      FutureExpenseDao futureExpenseDao, DeletedRecordDao deletedRecordDao) {
    return new SyncRepositoryImpl(context, supabaseProvider, cardDao, expenseDao, futureExpenseDao, deletedRecordDao);
  }
}
