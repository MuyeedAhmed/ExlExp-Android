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
public final class ParseZelleDetailsUseCase_Factory implements Factory<ParseZelleDetailsUseCase> {
  @Override
  public ParseZelleDetailsUseCase get() {
    return newInstance();
  }

  public static ParseZelleDetailsUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ParseZelleDetailsUseCase newInstance() {
    return new ParseZelleDetailsUseCase();
  }

  private static final class InstanceHolder {
    private static final ParseZelleDetailsUseCase_Factory INSTANCE = new ParseZelleDetailsUseCase_Factory();
  }
}
