package com.muyeedahmed.exlexp.data.repository;

import com.muyeedahmed.exlexp.data.local.dao.CardDao;
import com.muyeedahmed.exlexp.data.local.dao.DeletedRecordDao;
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
public final class CardRepositoryImpl_Factory implements Factory<CardRepositoryImpl> {
  private final Provider<CardDao> cardDaoProvider;

  private final Provider<DeletedRecordDao> deletedRecordDaoProvider;

  public CardRepositoryImpl_Factory(Provider<CardDao> cardDaoProvider,
      Provider<DeletedRecordDao> deletedRecordDaoProvider) {
    this.cardDaoProvider = cardDaoProvider;
    this.deletedRecordDaoProvider = deletedRecordDaoProvider;
  }

  @Override
  public CardRepositoryImpl get() {
    return newInstance(cardDaoProvider.get(), deletedRecordDaoProvider.get());
  }

  public static CardRepositoryImpl_Factory create(Provider<CardDao> cardDaoProvider,
      Provider<DeletedRecordDao> deletedRecordDaoProvider) {
    return new CardRepositoryImpl_Factory(cardDaoProvider, deletedRecordDaoProvider);
  }

  public static CardRepositoryImpl newInstance(CardDao cardDao, DeletedRecordDao deletedRecordDao) {
    return new CardRepositoryImpl(cardDao, deletedRecordDao);
  }
}
