package com.example.weatherphotos.di

import com.example.weatherphotos.data.remote.WeatherService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
object ServicesModule {
    @ViewModelScoped
    @Provides
    fun provideWallService(retrofit: Retrofit): WeatherService {
        return retrofit.create(WeatherService::class.java)
    }
}