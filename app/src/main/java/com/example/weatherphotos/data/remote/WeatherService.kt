package com.example.weatherphotos.data.remote

import com.example.weatherphotos.DomainConstants
import com.example.weatherphotos.models.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("/data/2.5/weather")
    suspend fun getWeatherData(
        @Query("lat") lat : Double,
        @Query("lon") long : Double,
        @Query("units") units : String = "metric",
        @Query("appid") appId : String = DomainConstants.getApiKey(),
    ): Response<WeatherResponse>
}