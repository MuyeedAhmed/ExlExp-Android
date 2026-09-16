package com.muyeedahmed.exlexp.di;

import com.muyeedahmed.exlexp.data.remote.SupabaseClientProvider;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class NetworkModule_ProvideSupabaseClientProviderFactory implements Factory<SupabaseClientProvider> {
  @Override
  public SupabaseClientProvider get() {
    return provideSupabaseClientProvider();
  }

  public static NetworkModule_ProvideSupabaseClientProviderFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SupabaseClientProvider provideSupabaseClientProvider() {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideSupabaseClientProvider());
  }

  private static final class InstanceHolder {
    private static final NetworkModule_ProvideSupabaseClientProviderFactory INSTANCE = new NetworkModule_ProvideSupabaseClientProviderFactory();
  }
}
