package com.example.weatherphotos.data.dao

import com.example.weatherphotos.domain.model.WeatherPhoto
import com.example.weatherphotos.domain.repo.WeatherDBRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherDBImp @Inject constructor(
    private val weatherDao : WeatherDao
) : WeatherDBRepository {
    override suspend fun saveWeatherPhoto(weatherPhoto: WeatherPhoto) = weatherDao.insertWeatherPhoto(weatherPhoto)

    override suspend fun getSavedPhotos(): Flow<List<WeatherPhoto>> = weatherDao.getWeatherPhotos()

    override suspend fun deletePhoto(weatherPhoto: WeatherPhoto)  = weatherDao.deletePhoto(weatherPhoto)
}