package com.example.weatherphotos

object DomainConstants {

    private const val apiKey : String = "87fe72463e8772252a3bff691245c771"
    const val FACEBOOK_PACKAGE = "com.facebook.katana"
    const val TWITTER_PACKAGE = "com.twitter.android"
    fun getApiKey() = apiKey
    fun getApiBaseUrl() = "https://api.openweathermap.org"

}