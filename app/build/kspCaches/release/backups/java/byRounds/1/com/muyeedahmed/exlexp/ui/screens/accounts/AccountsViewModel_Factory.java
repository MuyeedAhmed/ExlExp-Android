package com.muyeedahmed.exlexp.ui.screens.accounts;

import com.muyeedahmed.exlexp.domain.repository.CardRepository;
import com.muyeedahmed.exlexp.domain.repository.ExpenseRepository;
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
public final class AccountsViewModel_Factory implements Factory<AccountsViewModel> {
  private final Provider<CardRepository> cardRepositoryProvider;

  private final Provider<ExpenseRepository> expenseRepositoryProvider;

  private final Provider<SyncRepository> syncRepositoryProvider;

  public AccountsViewModel_Factory(Provider<CardRepository> cardRepositoryProvider,
      Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<SyncRepository> syncRepositoryProvider) {
    this.cardRepositoryProvider = cardRepositoryProvider;
    this.expenseRepositoryProvider = expenseRepositoryProvider;
    this.syncRepositoryProvider = syncRepositoryProvider;
  }

  @Override
  public AccountsViewModel get() {
    return newInstance(cardRepositoryProvider.get(), expenseRepositoryProvider.get(), syncRepositoryProvider.get());
  }

  public static AccountsViewModel_Factory create(Provider<CardRepository> cardRepositoryProvider,
      Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<SyncRepository> syncRepositoryProvider) {
    return new AccountsViewModel_Factory(cardRepositoryProvider, expenseRepositoryProvider, syncRepositoryProvider);
  }

  public static AccountsViewModel newInstance(CardRepository cardRepository,
      ExpenseRepository expenseRepository, SyncRepository syncRepository) {
    return new AccountsViewModel(cardRepository, expenseRepository, syncRepository);
  }
}
