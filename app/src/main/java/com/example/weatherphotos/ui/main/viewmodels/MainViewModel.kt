package com.example.weatherphotos.ui.main.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherphotos.helper.DataState
import com.example.weatherphotos.domain.use_cases.GetWeatherPhotosUseCase
import com.example.weatherphotos.domain.model.WeatherPhoto
import com.example.weatherphotos.domain.use_cases.DeleteWeatherPhotoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getWeatherPhotosUseCase: GetWeatherPhotosUseCase,
    private val deleteWeatherPhotoUseCase: DeleteWeatherPhotoUseCase
) : ViewModel(), IMainFragmentViewModel {

    private val _weatherPhotosList = MutableLiveData<DataState<List<WeatherPhoto>>>()

    private val weatherPhotosList : LiveData<DataState<List<WeatherPhoto>>> get() = _weatherPhotosList


    private val _deletePhoto = MutableLiveData<DataState<Int>>()

    private val deletePhoto : LiveData<DataState<Int>> get() = _deletePhoto

    override fun weatherPhotosResponse() = weatherPhotosList
    override fun deletePhotoResponse() = deletePhoto
    override fun getWeatherPhotos() {
        getWeatherPhotosUseCase.invoke().onEach { dataState ->
            when (dataState) {
                is DataState.Data ->
                    _weatherPhotosList.postValue(DataState.Data(dataState.data))
                is DataState.Error ->
                    _weatherPhotosList.postValue(DataState.Error(dataState.exception, dataState.message))
                is DataState.Loading ->
                    _weatherPhotosList.postValue(DataState.Loading(dataState.progressBarState))

            }
        }.launchIn(viewModelScope)
    }

    override fun deletePhoto(weatherPhoto: WeatherPhoto) {
        deleteWeatherPhotoUseCase.invoke(weatherPhoto).onEach {dataState->
            when (dataState) {
                is DataState.Data ->
                    _deletePhoto.postValue(DataState.Data(1))
                is DataState.Error ->
                    _deletePhoto.postValue(DataState.Error(dataState.exception, dataState.message))
                is DataState.Loading ->
                    _deletePhoto.postValue(DataState.Loading(dataState.progressBarState))
            }
        }.launchIn(viewModelScope)
    }
}