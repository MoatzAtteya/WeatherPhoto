package com.example.weatherphotos.ui.photo_prepare.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherphotos.DataState
import com.example.weatherphotos.ProgressBarState
import com.example.weatherphotos.domain.use_cases.GetWeatherUseCase
import com.example.weatherphotos.models.WeatherResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class PhotoPrepareViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel(), IPhotoPrepareViewModel {

    private val _weatherStatusResponse: MutableStateFlow<DataState<WeatherResponse>> = MutableStateFlow(
        DataState.Loading(ProgressBarState.Loading)
    )

    override fun weatherStatusResponse() = _weatherStatusResponse

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
}