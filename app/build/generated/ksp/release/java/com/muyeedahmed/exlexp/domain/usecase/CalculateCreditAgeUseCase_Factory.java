package com.muyeedahmed.exlexp.domain.usecase;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class CalculateCreditAgeUseCase_Factory implements Factory<CalculateCreditAgeUseCase> {
  @Override
  public CalculateCreditAgeUseCase get() {
    return newInstance();
  }

  public static CalculateCreditAgeUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CalculateCreditAgeUseCase newInstance() {
    return new CalculateCreditAgeUseCase();
  }

  private static final class InstanceHolder {
    private static final CalculateCreditAgeUseCase_Factory INSTANCE = new CalculateCreditAgeUseCase_Factory();
  }
}
