package com.muyeedahmed.exlexp.di;

import com.muyeedahmed.exlexp.data.local.AppDatabase;
import com.muyeedahmed.exlexp.data.local.dao.DeletedRecordDao;
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
public final class DatabaseModule_ProvideDeletedRecordDaoFactory implements Factory<DeletedRecordDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideDeletedRecordDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public DeletedRecordDao get() {
    return provideDeletedRecordDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideDeletedRecordDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideDeletedRecordDaoFactory(databaseProvider);
  }

  public static DeletedRecordDao provideDeletedRecordDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideDeletedRecordDao(database));
  }
}
