package com.example.weatherphotos.di

import com.example.weatherphotos.DomainConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun providesBaseUrl() = DomainConstants.getApiBaseUrl()


    @Singleton
    @Provides
    fun providesLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Singleton
    @Provides
    fun providesHttpClient(logger: HttpLoggingInterceptor) : OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.addInterceptor(logger)
        okHttpClient.callTimeout(60L , TimeUnit.SECONDS)
        okHttpClient.connectTimeout(60L , TimeUnit.SECONDS)
        okHttpClient.writeTimeout(60L , TimeUnit.SECONDS)
        okHttpClient.readTimeout(60L , TimeUnit.SECONDS)
        okHttpClient.addInterceptor {
            val url = it.request().url.newBuilder().build()

            val newRequest: Request = it.request().newBuilder()
                .url(url)
                .header("Accept", "application/json")
                .build()
            it.proceed(newRequest)
        }
        return okHttpClient.build()
    }


    @Singleton
    @Provides
    fun providesConverterFactory(): Converter.Factory{
        return GsonConverterFactory.create()
    }

    @Singleton
    @Provides
    fun providesBaseRetrofit(base_url: String, colverFactory: Converter.Factory, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(base_url)
            .addConverterFactory(colverFactory)
            .client(okHttpClient)
            .build()
    }

}