package com.muyeedahmed.exlexp.data.repository;

import com.muyeedahmed.exlexp.data.local.dao.DeletedRecordDao;
import com.muyeedahmed.exlexp.data.local.dao.FutureExpenseDao;
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
public final class FutureExpenseRepositoryImpl_Factory implements Factory<FutureExpenseRepositoryImpl> {
  private final Provider<FutureExpenseDao> futureExpenseDaoProvider;

  private final Provider<DeletedRecordDao> deletedRecordDaoProvider;

  public FutureExpenseRepositoryImpl_Factory(Provider<FutureExpenseDao> futureExpenseDaoProvider,
      Provider<DeletedRecordDao> deletedRecordDaoProvider) {
    this.futureExpenseDaoProvider = futureExpenseDaoProvider;
    this.deletedRecordDaoProvider = deletedRecordDaoProvider;
  }

  @Override
  public FutureExpenseRepositoryImpl get() {
    return newInstance(futureExpenseDaoProvider.get(), deletedRecordDaoProvider.get());
  }

  public static FutureExpenseRepositoryImpl_Factory create(
      Provider<FutureExpenseDao> futureExpenseDaoProvider,
      Provider<DeletedRecordDao> deletedRecordDaoProvider) {
    return new FutureExpenseRepositoryImpl_Factory(futureExpenseDaoProvider, deletedRecordDaoProvider);
  }

  public static FutureExpenseRepositoryImpl newInstance(FutureExpenseDao futureExpenseDao,
      DeletedRecordDao deletedRecordDao) {
    return new FutureExpenseRepositoryImpl(futureExpenseDao, deletedRecordDao);
  }
}
