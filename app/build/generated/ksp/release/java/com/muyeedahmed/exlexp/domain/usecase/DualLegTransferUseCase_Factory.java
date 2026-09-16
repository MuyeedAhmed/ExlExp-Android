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
public final class DualLegTransferUseCase_Factory implements Factory<DualLegTransferUseCase> {
  @Override
  public DualLegTransferUseCase get() {
    return newInstance();
  }

  public static DualLegTransferUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static DualLegTransferUseCase newInstance() {
    return new DualLegTransferUseCase();
  }

  private static final class InstanceHolder {
    private static final DualLegTransferUseCase_Factory INSTANCE = new DualLegTransferUseCase_Factory();
  }
}
