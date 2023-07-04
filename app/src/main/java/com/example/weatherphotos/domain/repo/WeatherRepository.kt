package com.example.weatherphotos.domain.repo

import com.example.weatherphotos.domain.model.WeatherResponse
import retrofit2.Response

interface WeatherRepository {
    suspend fun getCurrentWeather(lat : Double , long: Double) : Response<WeatherResponse>
}