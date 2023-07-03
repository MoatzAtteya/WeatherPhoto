package com.example.weatherphotos.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("main")
    @Expose
    val main: Weather,
    @SerializedName("name")
    @Expose
    val name: String,
    @SerializedName("weather")
    @Expose
    val weather : ArrayList<WeatherCondition>,
)