package com.muyeedahmed.exlexp.ui.screens.creditcards;

import com.muyeedahmed.exlexp.domain.repository.CardRepository;
import com.muyeedahmed.exlexp.domain.repository.ExpenseRepository;
import com.muyeedahmed.exlexp.domain.repository.SyncRepository;
import com.muyeedahmed.exlexp.domain.usecase.CalculateCreditAgeUseCase;
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
public final class CreditCardsViewModel_Factory implements Factory<CreditCardsViewModel> {
  private final Provider<CardRepository> cardRepositoryProvider;

  private final Provider<ExpenseRepository> expenseRepositoryProvider;

  private final Provider<SyncRepository> syncRepositoryProvider;

  private final Provider<CalculateCreditAgeUseCase> calculateCreditAgeUseCaseProvider;

  public CreditCardsViewModel_Factory(Provider<CardRepository> cardRepositoryProvider,
      Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<SyncRepository> syncRepositoryProvider,
      Provider<CalculateCreditAgeUseCase> calculateCreditAgeUseCaseProvider) {
    this.cardRepositoryProvider = cardRepositoryProvider;
    this.expenseRepositoryProvider = expenseRepositoryProvider;
    this.syncRepositoryProvider = syncRepositoryProvider;
    this.calculateCreditAgeUseCaseProvider = calculateCreditAgeUseCaseProvider;
  }

  @Override
  public CreditCardsViewModel get() {
    return newInstance(cardRepositoryProvider.get(), expenseRepositoryProvider.get(), syncRepositoryProvider.get(), calculateCreditAgeUseCaseProvider.get());
  }

  public static CreditCardsViewModel_Factory create(Provider<CardRepository> cardRepositoryProvider,
      Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<SyncRepository> syncRepositoryProvider,
      Provider<CalculateCreditAgeUseCase> calculateCreditAgeUseCaseProvider) {
    return new CreditCardsViewModel_Factory(cardRepositoryProvider, expenseRepositoryProvider, syncRepositoryProvider, calculateCreditAgeUseCaseProvider);
  }

  public static CreditCardsViewModel newInstance(CardRepository cardRepository,
      ExpenseRepository expenseRepository, SyncRepository syncRepository,
      CalculateCreditAgeUseCase calculateCreditAgeUseCase) {
    return new CreditCardsViewModel(cardRepository, expenseRepository, syncRepository, calculateCreditAgeUseCase);
  }
}
