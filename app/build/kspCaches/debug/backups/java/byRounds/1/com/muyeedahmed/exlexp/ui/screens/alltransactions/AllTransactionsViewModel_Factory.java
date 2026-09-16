package com.muyeedahmed.exlexp.ui.screens.alltransactions;

import com.muyeedahmed.exlexp.domain.repository.CardRepository;
import com.muyeedahmed.exlexp.domain.repository.ExpenseRepository;
import com.muyeedahmed.exlexp.domain.repository.SyncRepository;
import com.muyeedahmed.exlexp.domain.usecase.ConsolidateTransfersUseCase;
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
public final class AllTransactionsViewModel_Factory implements Factory<AllTransactionsViewModel> {
  private final Provider<CardRepository> cardRepositoryProvider;

  private final Provider<ExpenseRepository> expenseRepositoryProvider;

  private final Provider<SyncRepository> syncRepositoryProvider;

  private final Provider<ConsolidateTransfersUseCase> consolidateTransfersUseCaseProvider;

  public AllTransactionsViewModel_Factory(Provider<CardRepository> cardRepositoryProvider,
      Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<SyncRepository> syncRepositoryProvider,
      Provider<ConsolidateTransfersUseCase> consolidateTransfersUseCaseProvider) {
    this.cardRepositoryProvider = cardRepositoryProvider;
    this.expenseRepositoryProvider = expenseRepositoryProvider;
    this.syncRepositoryProvider = syncRepositoryProvider;
    this.consolidateTransfersUseCaseProvider = consolidateTransfersUseCaseProvider;
  }

  @Override
  public AllTransactionsViewModel get() {
    return newInstance(cardRepositoryProvider.get(), expenseRepositoryProvider.get(), syncRepositoryProvider.get(), consolidateTransfersUseCaseProvider.get());
  }

  public static AllTransactionsViewModel_Factory create(
      Provider<CardRepository> cardRepositoryProvider,
      Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<SyncRepository> syncRepositoryProvider,
      Provider<ConsolidateTransfersUseCase> consolidateTransfersUseCaseProvider) {
    return new AllTransactionsViewModel_Factory(cardRepositoryProvider, expenseRepositoryProvider, syncRepositoryProvider, consolidateTransfersUseCaseProvider);
  }

  public static AllTransactionsViewModel newInstance(CardRepository cardRepository,
      ExpenseRepository expenseRepository, SyncRepository syncRepository,
      ConsolidateTransfersUseCase consolidateTransfersUseCase) {
    return new AllTransactionsViewModel(cardRepository, expenseRepository, syncRepository, consolidateTransfersUseCase);
  }
}
