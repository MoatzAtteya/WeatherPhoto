package com.example.weatherphotos.data.remote

import javax.inject.Inject

class WeatherImpl @Inject constructor(
    private val api : WeatherService
) : WeatherRepository {
    override suspend fun getCurrentWeather(lat: Double, long: Double) = api.getWeatherData(
        lat, long
    )
}