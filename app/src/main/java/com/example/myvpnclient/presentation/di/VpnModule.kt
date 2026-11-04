package com.example.myvpnclient.presentation.di

import com.example.myvpnclient.data.PreferenceRepositoryImpl
import com.example.myvpnclient.data.VpnApi
import com.example.myvpnclient.data.ShakeHandsRepositoryImpl
import com.example.myvpnclient.domain.PreferenceRepository
import com.example.myvpnclient.domain.ShakeHandsRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
abstract class VpnModule {

    @Binds
    @Singleton
    abstract fun bindShakeHandsRepository(impl: ShakeHandsRepositoryImpl): ShakeHandsRepository

    @Binds
    @Singleton
    abstract fun bindPreferenceRepository(impl: PreferenceRepositoryImpl): PreferenceRepository

    @Module
    companion object {
        @Provides
        @Singleton
        fun provideJson(): Json = Json { ignoreUnknownKeys = true }

        @Provides
        @Singleton
        fun provideRetrofit(json: Json): Retrofit = Retrofit.Builder()
            .baseUrl("http://localhost/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        @Provides
        fun provideVpnApi(retrofit: Retrofit): VpnApi = retrofit.create(VpnApi::class.java)
    }
}