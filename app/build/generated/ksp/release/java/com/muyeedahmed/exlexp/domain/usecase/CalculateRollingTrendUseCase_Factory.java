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
public final class CalculateRollingTrendUseCase_Factory implements Factory<CalculateRollingTrendUseCase> {
  @Override
  public CalculateRollingTrendUseCase get() {
    return newInstance();
  }

  public static CalculateRollingTrendUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CalculateRollingTrendUseCase newInstance() {
    return new CalculateRollingTrendUseCase();
  }

  private static final class InstanceHolder {
    private static final CalculateRollingTrendUseCase_Factory INSTANCE = new CalculateRollingTrendUseCase_Factory();
  }
}
