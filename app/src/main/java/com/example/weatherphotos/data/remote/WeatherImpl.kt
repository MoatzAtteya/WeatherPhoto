package com.example.weatherphotos.data.remote

import com.example.weatherphotos.domain.repo.WeatherRepository
import javax.inject.Inject

class WeatherImpl @Inject constructor(
    private val api : WeatherService
) : WeatherRepository {
    override suspend fun getCurrentWeather(lat: Double, long: Double) = api.getWeatherData(
        lat, long
    )
}