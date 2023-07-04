package com.example.weatherphotos.ui.main.viewmodels

import androidx.lifecycle.LiveData
import com.example.weatherphotos.helper.DataState
import com.example.weatherphotos.base.IBaseViewModel
import com.example.weatherphotos.domain.model.WeatherPhoto

interface IMainFragmentViewModel : IBaseViewModel {

    fun weatherPhotosResponse() : LiveData<DataState<List<WeatherPhoto>>>

    fun getWeatherPhotos()

    fun deletePhoto(weatherPhoto: WeatherPhoto)

    fun deletePhotoResponse() : LiveData<DataState<Int>>

}