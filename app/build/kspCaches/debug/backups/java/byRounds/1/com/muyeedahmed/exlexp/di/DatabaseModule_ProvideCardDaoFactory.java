package com.muyeedahmed.exlexp.di;

import com.muyeedahmed.exlexp.data.local.AppDatabase;
import com.muyeedahmed.exlexp.data.local.dao.CardDao;
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
public final class DatabaseModule_ProvideCardDaoFactory implements Factory<CardDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideCardDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public CardDao get() {
    return provideCardDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideCardDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideCardDaoFactory(databaseProvider);
  }

  public static CardDao provideCardDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideCardDao(database));
  }
}
