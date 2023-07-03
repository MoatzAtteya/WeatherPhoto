package com.example.weatherphotos

object DomainConstants {

    private const val apiKey : String = "87fe72463e8772252a3bff691245c771"

    const val LAT_EXTRA_KEY = "latitude"
    const val LONG_EXTRA_KEY = "longitude"
    const val IMAGE_PATH_EXTRA_KEY = "imagePath"

    fun getApiKey() = apiKey
    fun getApiBaseUrl() = "https://api.openweathermap.org"

}