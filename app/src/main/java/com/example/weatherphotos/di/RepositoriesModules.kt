package com.example.weatherphotos.di

import com.example.weatherphotos.data.remote.WeatherImpl
import com.example.weatherphotos.domain.repo.WeatherRepository
import com.example.weatherphotos.data.remote.WeatherService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RepositoriesModules {

    @ViewModelScoped
    @Provides
    fun provideWallRepository(service: WeatherService): WeatherRepository {
        return WeatherImpl(service)
    }
}