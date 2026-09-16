package com.muyeedahmed.exlexp.ui.screens.logexpense;

import com.muyeedahmed.exlexp.domain.repository.CardRepository;
import com.muyeedahmed.exlexp.domain.repository.ExpenseRepository;
import com.muyeedahmed.exlexp.domain.repository.SyncRepository;
import com.muyeedahmed.exlexp.domain.usecase.DualLegTransferUseCase;
import com.muyeedahmed.exlexp.domain.usecase.ParseZelleDetailsUseCase;
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
public final class LogExpenseViewModel_Factory implements Factory<LogExpenseViewModel> {
  private final Provider<CardRepository> cardRepositoryProvider;

  private final Provider<ExpenseRepository> expenseRepositoryProvider;

  private final Provider<SyncRepository> syncRepositoryProvider;

  private final Provider<DualLegTransferUseCase> dualLegTransferUseCaseProvider;

  private final Provider<ParseZelleDetailsUseCase> parseZelleDetailsUseCaseProvider;

  public LogExpenseViewModel_Factory(Provider<CardRepository> cardRepositoryProvider,
      Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<SyncRepository> syncRepositoryProvider,
      Provider<DualLegTransferUseCase> dualLegTransferUseCaseProvider,
      Provider<ParseZelleDetailsUseCase> parseZelleDetailsUseCaseProvider) {
    this.cardRepositoryProvider = cardRepositoryProvider;
    this.expenseRepositoryProvider = expenseRepositoryProvider;
    this.syncRepositoryProvider = syncRepositoryProvider;
    this.dualLegTransferUseCaseProvider = dualLegTransferUseCaseProvider;
    this.parseZelleDetailsUseCaseProvider = parseZelleDetailsUseCaseProvider;
  }

  @Override
  public LogExpenseViewModel get() {
    return newInstance(cardRepositoryProvider.get(), expenseRepositoryProvider.get(), syncRepositoryProvider.get(), dualLegTransferUseCaseProvider.get(), parseZelleDetailsUseCaseProvider.get());
  }

  public static LogExpenseViewModel_Factory create(Provider<CardRepository> cardRepositoryProvider,
      Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<SyncRepository> syncRepositoryProvider,
      Provider<DualLegTransferUseCase> dualLegTransferUseCaseProvider,
      Provider<ParseZelleDetailsUseCase> parseZelleDetailsUseCaseProvider) {
    return new LogExpenseViewModel_Factory(cardRepositoryProvider, expenseRepositoryProvider, syncRepositoryProvider, dualLegTransferUseCaseProvider, parseZelleDetailsUseCaseProvider);
  }

  public static LogExpenseViewModel newInstance(CardRepository cardRepository,
      ExpenseRepository expenseRepository, SyncRepository syncRepository,
      DualLegTransferUseCase dualLegTransferUseCase,
      ParseZelleDetailsUseCase parseZelleDetailsUseCase) {
    return new LogExpenseViewModel(cardRepository, expenseRepository, syncRepository, dualLegTransferUseCase, parseZelleDetailsUseCase);
  }
}
