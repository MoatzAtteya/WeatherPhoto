package com.example.weatherphotos.di

import com.example.weatherphotos.data.dao.WeatherDBImp
import com.example.weatherphotos.data.dao.WeatherDBRepository
import com.example.weatherphotos.data.dao.WeatherDao
import com.example.weatherphotos.data.remote.WeatherImpl
import com.example.weatherphotos.data.remote.WeatherRepository
import com.example.weatherphotos.data.remote.WeatherService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
object RepositoriesModules {

    @ViewModelScoped
    @Provides
    fun provideWallRepository(service: WeatherService): WeatherRepository {
        return WeatherImpl(service)
    }

    @Provides
    @Singleton
    fun provideNewsDBRepository(weatherDao: WeatherDao): WeatherDBRepository {
        return WeatherDBImp(weatherDao)
    }
}