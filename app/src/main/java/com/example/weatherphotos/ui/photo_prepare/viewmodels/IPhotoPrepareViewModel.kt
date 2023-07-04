package com.example.weatherphotos.ui.photo_prepare.viewmodels

import com.example.weatherphotos.helper.DataState
import com.example.weatherphotos.base.IBaseViewModel
import com.example.weatherphotos.domain.model.WeatherPhoto
import com.example.weatherphotos.domain.model.WeatherResponse
import kotlinx.coroutines.flow.StateFlow

interface IPhotoPrepareViewModel : IBaseViewModel  {

    fun weatherStatusResponse() : StateFlow<DataState<WeatherResponse>>
    fun saveWeatherPhotoResponse() : StateFlow<DataState<Long>>

    fun getWeatherStatus( lat : Double , long: Double )

    fun saveWeatherPhoto(weatherPhoto : WeatherPhoto)
}