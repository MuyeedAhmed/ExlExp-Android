package com.muyeedahmed.exlexp.di;

import com.muyeedahmed.exlexp.data.local.AppDatabase;
import com.muyeedahmed.exlexp.data.local.dao.FutureExpenseDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class DatabaseModule_ProvideFutureExpenseDaoFactory implements Factory<FutureExpenseDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideFutureExpenseDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public FutureExpenseDao get() {
    return provideFutureExpenseDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideFutureExpenseDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideFutureExpenseDaoFactory(databaseProvider);
  }

  public static FutureExpenseDao provideFutureExpenseDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideFutureExpenseDao(database));
  }
}
