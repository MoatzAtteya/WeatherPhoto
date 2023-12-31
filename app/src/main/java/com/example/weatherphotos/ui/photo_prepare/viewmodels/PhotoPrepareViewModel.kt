package com.example.weatherphotos.ui.photo_prepare.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherphotos.helper.DataState
import com.example.weatherphotos.helper.ProgressBarState
import com.example.weatherphotos.domain.use_cases.GetWeatherUseCase
import com.example.weatherphotos.domain.use_cases.SavePhotoUseCase
import com.example.weatherphotos.domain.model.WeatherPhoto
import com.example.weatherphotos.domain.model.WeatherResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class PhotoPrepareViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val savePhotoUseCase: SavePhotoUseCase,
) : ViewModel(), IPhotoPrepareViewModel {

    private val _weatherStatusResponse: MutableStateFlow<DataState<WeatherResponse>> = MutableStateFlow(
        DataState.Loading(ProgressBarState.Idle)
    )
    private val _savePhotoResponse: MutableStateFlow<DataState<Long>> = MutableStateFlow(
        DataState.Loading(ProgressBarState.Idle)
    )

    override fun weatherStatusResponse() = _weatherStatusResponse

    override fun saveWeatherPhotoResponse() = _savePhotoResponse

    override fun getWeatherStatus(lat: Double, long: Double) {
        getWeatherUseCase.invoke(lat,long).onEach { dataState ->
            when (dataState) {
                is DataState.Data ->
                    _weatherStatusResponse.value = DataState.Data(dataState.data)
                is DataState.Error ->
                    _weatherStatusResponse.value =
                        DataState.Error(dataState.exception, dataState.message)

                is DataState.Loading ->
                    _weatherStatusResponse.value =
                        DataState.Loading(dataState.progressBarState)
            }
        }.launchIn(viewModelScope)
    }

    override fun saveWeatherPhoto(weatherPhoto: WeatherPhoto) {
        savePhotoUseCase.invoke(weatherPhoto).onEach { dataState ->
            when (dataState) {
                is DataState.Data ->
                    _savePhotoResponse.value = DataState.Data(dataState.data)
                is DataState.Error ->
                    _savePhotoResponse.value =
                        DataState.Error(dataState.exception, dataState.message)

                is DataState.Loading ->
                    _savePhotoResponse.value =
                        DataState.Loading(dataState.progressBarState)
            }
        }.launchIn(viewModelScope)
    }
}