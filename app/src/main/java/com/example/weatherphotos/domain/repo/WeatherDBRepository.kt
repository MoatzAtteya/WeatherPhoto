package com.example.weatherphotos.domain.repo

import com.example.weatherphotos.domain.model.WeatherPhoto
import kotlinx.coroutines.flow.Flow

interface WeatherDBRepository {
    suspend fun saveWeatherPhoto(weatherPhoto: WeatherPhoto) :Long
    suspend fun getSavedPhotos() : Flow<List<WeatherPhoto>>

    suspend fun deletePhoto(weatherPhoto: WeatherPhoto)
}