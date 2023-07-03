package com.example.weatherphotos.ui.photo_prepare.viewmodels

import com.example.weatherphotos.DataState
import com.example.weatherphotos.base.IBaseViewModel
import com.example.weatherphotos.models.WeatherPhoto
import com.example.weatherphotos.models.WeatherResponse
import kotlinx.coroutines.flow.StateFlow

interface IPhotoPrepareViewModel : IBaseViewModel  {

    fun weatherStatusResponse() : StateFlow<DataState<WeatherResponse>>
    fun saveWeatherPhotoResponse() : StateFlow<DataState<Long>>

    fun getWeatherStatus( lat : Double , long: Double )

    fun saveWeatherPhoto(weatherPhoto : WeatherPhoto)
}