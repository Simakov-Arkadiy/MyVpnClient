package com.example.myvpnclient.presentation.DI

import com.example.myvpnclient.data.VpnApi
import com.example.myvpnclient.data.VpnRepositoryImpl
import com.example.myvpnclient.domain.VpnRepository
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
    abstract fun bindVpnRepository(impl: VpnRepositoryImpl): VpnRepository

    @Module
    companion object {
        @Provides
        @Singleton
        fun provideJson(): Json = Json { ignoreUnknownKeys = true }

        @Provides
        @Singleton
        fun provideRetrofit(json: Json): Retrofit = Retrofit.Builder()
            .baseUrl("https://api.example.com/v1/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        @Provides
        fun provideVpnApi(retrofit: Retrofit): VpnApi = retrofit.create(VpnApi::class.java)
    }
}