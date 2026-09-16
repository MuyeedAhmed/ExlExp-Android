package com.muyeedahmed.exlexp.data.repository;

import com.muyeedahmed.exlexp.data.local.dao.DeletedRecordDao;
import com.muyeedahmed.exlexp.data.local.dao.ExpenseDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
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
public final class ExpenseRepositoryImpl_Factory implements Factory<ExpenseRepositoryImpl> {
  private final Provider<ExpenseDao> expenseDaoProvider;

  private final Provider<DeletedRecordDao> deletedRecordDaoProvider;

  public ExpenseRepositoryImpl_Factory(Provider<ExpenseDao> expenseDaoProvider,
      Provider<DeletedRecordDao> deletedRecordDaoProvider) {
    this.expenseDaoProvider = expenseDaoProvider;
    this.deletedRecordDaoProvider = deletedRecordDaoProvider;
  }

  @Override
  public ExpenseRepositoryImpl get() {
    return newInstance(expenseDaoProvider.get(), deletedRecordDaoProvider.get());
  }

  public static ExpenseRepositoryImpl_Factory create(Provider<ExpenseDao> expenseDaoProvider,
      Provider<DeletedRecordDao> deletedRecordDaoProvider) {
    return new ExpenseRepositoryImpl_Factory(expenseDaoProvider, deletedRecordDaoProvider);
  }

  public static ExpenseRepositoryImpl newInstance(ExpenseDao expenseDao,
      DeletedRecordDao deletedRecordDao) {
    return new ExpenseRepositoryImpl(expenseDao, deletedRecordDao);
  }
}
