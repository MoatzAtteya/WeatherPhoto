package com.example.weatherphotos

object DomainConstants {

    const val FACEBOOK_PACKAGE = "com.facebook.katana"
    const val TWITTER_PACKAGE = "com.twitter.android"
    fun getApiKey() = BuildConfig.API_KEY
    fun getApiBaseUrl() = "https://api.openweathermap.org"

}