package com.muyeedahmed.exlexp.ui.screens.dashboard;

import com.muyeedahmed.exlexp.domain.repository.CardRepository;
import com.muyeedahmed.exlexp.domain.repository.ExpenseRepository;
import com.muyeedahmed.exlexp.domain.repository.FutureExpenseRepository;
import com.muyeedahmed.exlexp.domain.repository.SyncRepository;
import com.muyeedahmed.exlexp.domain.usecase.CalculateBalancesUseCase;
import com.muyeedahmed.exlexp.domain.usecase.CalculateRollingTrendUseCase;
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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<CardRepository> cardRepositoryProvider;

  private final Provider<ExpenseRepository> expenseRepositoryProvider;

  private final Provider<FutureExpenseRepository> futureExpenseRepositoryProvider;

  private final Provider<SyncRepository> syncRepositoryProvider;

  private final Provider<CalculateBalancesUseCase> calculateBalancesUseCaseProvider;

  private final Provider<CalculateRollingTrendUseCase> calculateRollingTrendUseCaseProvider;

  private final Provider<ConsolidateTransfersUseCase> consolidateTransfersUseCaseProvider;

  public DashboardViewModel_Factory(Provider<CardRepository> cardRepositoryProvider,
      Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<FutureExpenseRepository> futureExpenseRepositoryProvider,
      Provider<SyncRepository> syncRepositoryProvider,
      Provider<CalculateBalancesUseCase> calculateBalancesUseCaseProvider,
      Provider<CalculateRollingTrendUseCase> calculateRollingTrendUseCaseProvider,
      Provider<ConsolidateTransfersUseCase> consolidateTransfersUseCaseProvider) {
    this.cardRepositoryProvider = cardRepositoryProvider;
    this.expenseRepositoryProvider = expenseRepositoryProvider;
    this.futureExpenseRepositoryProvider = futureExpenseRepositoryProvider;
    this.syncRepositoryProvider = syncRepositoryProvider;
    this.calculateBalancesUseCaseProvider = calculateBalancesUseCaseProvider;
    this.calculateRollingTrendUseCaseProvider = calculateRollingTrendUseCaseProvider;
    this.consolidateTransfersUseCaseProvider = consolidateTransfersUseCaseProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(cardRepositoryProvider.get(), expenseRepositoryProvider.get(), futureExpenseRepositoryProvider.get(), syncRepositoryProvider.get(), calculateBalancesUseCaseProvider.get(), calculateRollingTrendUseCaseProvider.get(), consolidateTransfersUseCaseProvider.get());
  }

  public static DashboardViewModel_Factory create(Provider<CardRepository> cardRepositoryProvider,
      Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<FutureExpenseRepository> futureExpenseRepositoryProvider,
      Provider<SyncRepository> syncRepositoryProvider,
      Provider<CalculateBalancesUseCase> calculateBalancesUseCaseProvider,
      Provider<CalculateRollingTrendUseCase> calculateRollingTrendUseCaseProvider,
      Provider<ConsolidateTransfersUseCase> consolidateTransfersUseCaseProvider) {
    return new DashboardViewModel_Factory(cardRepositoryProvider, expenseRepositoryProvider, futureExpenseRepositoryProvider, syncRepositoryProvider, calculateBalancesUseCaseProvider, calculateRollingTrendUseCaseProvider, consolidateTransfersUseCaseProvider);
  }

  public static DashboardViewModel newInstance(CardRepository cardRepository,
      ExpenseRepository expenseRepository, FutureExpenseRepository futureExpenseRepository,
      SyncRepository syncRepository, CalculateBalancesUseCase calculateBalancesUseCase,
      CalculateRollingTrendUseCase calculateRollingTrendUseCase,
      ConsolidateTransfersUseCase consolidateTransfersUseCase) {
    return new DashboardViewModel(cardRepository, expenseRepository, futureExpenseRepository, syncRepository, calculateBalancesUseCase, calculateRollingTrendUseCase, consolidateTransfersUseCase);
  }
}
