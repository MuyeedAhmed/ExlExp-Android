package com.muyeedahmed.exlexp.ui.screens.settings;

import com.muyeedahmed.exlexp.domain.repository.CardRepository;
import com.muyeedahmed.exlexp.domain.repository.SyncRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<CardRepository> cardRepositoryProvider;

  private final Provider<SyncRepository> syncRepositoryProvider;

  public SettingsViewModel_Factory(Provider<CardRepository> cardRepositoryProvider,
      Provider<SyncRepository> syncRepositoryProvider) {
    this.cardRepositoryProvider = cardRepositoryProvider;
    this.syncRepositoryProvider = syncRepositoryProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(cardRepositoryProvider.get(), syncRepositoryProvider.get());
  }

  public static SettingsViewModel_Factory create(Provider<CardRepository> cardRepositoryProvider,
      Provider<SyncRepository> syncRepositoryProvider) {
    return new SettingsViewModel_Factory(cardRepositoryProvider, syncRepositoryProvider);
  }

  public static SettingsViewModel newInstance(CardRepository cardRepository,
      SyncRepository syncRepository) {
    return new SettingsViewModel(cardRepository, syncRepository);
  }
}
