package com.example.weatherphotos.data.dao

import com.example.weatherphotos.models.WeatherPhoto
import kotlinx.coroutines.flow.Flow

interface WeatherDBRepository {
    suspend fun saveWeatherPhoto(weatherPhoto: WeatherPhoto) :Long
    suspend fun getSavedPhotos() : Flow<List<WeatherPhoto>>
}