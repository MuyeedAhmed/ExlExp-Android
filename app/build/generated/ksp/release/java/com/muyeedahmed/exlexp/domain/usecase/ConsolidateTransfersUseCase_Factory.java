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
public final class ConsolidateTransfersUseCase_Factory implements Factory<ConsolidateTransfersUseCase> {
  @Override
  public ConsolidateTransfersUseCase get() {
    return newInstance();
  }

  public static ConsolidateTransfersUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ConsolidateTransfersUseCase newInstance() {
    return new ConsolidateTransfersUseCase();
  }

  private static final class InstanceHolder {
    private static final ConsolidateTransfersUseCase_Factory INSTANCE = new ConsolidateTransfersUseCase_Factory();
  }
}
