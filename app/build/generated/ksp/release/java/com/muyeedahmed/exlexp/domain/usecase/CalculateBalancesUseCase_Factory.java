package com.muyeedahmed.exlexp.domain.usecase;

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
public final class CalculateBalancesUseCase_Factory implements Factory<CalculateBalancesUseCase> {
  private final Provider<CalculateCreditAgeUseCase> calculateCreditAgeUseCaseProvider;

  public CalculateBalancesUseCase_Factory(
      Provider<CalculateCreditAgeUseCase> calculateCreditAgeUseCaseProvider) {
    this.calculateCreditAgeUseCaseProvider = calculateCreditAgeUseCaseProvider;
  }

  @Override
  public CalculateBalancesUseCase get() {
    return newInstance(calculateCreditAgeUseCaseProvider.get());
  }

  public static CalculateBalancesUseCase_Factory create(
      Provider<CalculateCreditAgeUseCase> calculateCreditAgeUseCaseProvider) {
    return new CalculateBalancesUseCase_Factory(calculateCreditAgeUseCaseProvider);
  }

  public static CalculateBalancesUseCase newInstance(
      CalculateCreditAgeUseCase calculateCreditAgeUseCase) {
    return new CalculateBalancesUseCase(calculateCreditAgeUseCase);
  }
}
