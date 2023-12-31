package com.example.weatherphotos.di

import android.content.Context
import androidx.room.Room
import com.example.weatherphotos.data.dao.WeatherDBImp
import com.example.weatherphotos.domain.repo.WeatherDBRepository
import com.example.weatherphotos.data.dao.WeatherDao
import com.example.weatherphotos.data.dao.WeatherDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {
    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext appContext: Context): WeatherDataBase {
        return Room.databaseBuilder(
            appContext,
            WeatherDataBase::class.java,
            "WeatherPhotos"
        ).build()
    }

    @Provides
    @Singleton
    fun providesNewsDao(appDatabase: WeatherDataBase): WeatherDao {
        return appDatabase.weatherDao()
    }
    @Provides
    @Singleton
    fun provideNewsDBRepository(weatherDao: WeatherDao): WeatherDBRepository {
        return WeatherDBImp(weatherDao)
    }
}