package com.example.weatherphotos.data.remote

import com.example.weatherphotos.models.WeatherResponse
import retrofit2.Response

interface WeatherRepository {
    suspend fun getCurrentWeather(lat : Double , long: Double) : Response<WeatherResponse>
}