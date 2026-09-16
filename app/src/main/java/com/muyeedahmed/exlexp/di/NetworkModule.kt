package com.muyeedahmed.exlexp.di

import com.muyeedahmed.exlexp.data.remote.SupabaseClientProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideSupabaseClientProvider(): SupabaseClientProvider {
        return SupabaseClientProvider()
    }
}
