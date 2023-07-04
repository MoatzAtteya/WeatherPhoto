package com.example.weatherphotos.domain.use_cases

import com.example.weatherphotos.domain.model.WeatherPhoto
import com.example.weatherphotos.helper.DataState
import com.example.weatherphotos.helper.ProgressBarState
import com.example.weatherphotos.helper.ResponseCodeHandler
import com.example.weatherphotos.domain.repo.WeatherDBRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DeleteWeatherPhotoUseCase @Inject constructor(
    private val repository: WeatherDBRepository
) {
    fun invoke(weatherPhoto: WeatherPhoto): Flow<DataState<Int>> = flow {
        try {
            emit(DataState.Loading(ProgressBarState.Loading))
            repository.deletePhoto(weatherPhoto)
            emit(DataState.Data(1))
            emit(DataState.Loading(ProgressBarState.Idle))

        } catch (ex: Exception) {
            ex.printStackTrace()
            emit(DataState.Loading(ProgressBarState.Idle))
            emit(DataState.Error(ResponseCodeHandler.SERVER_ERROR, ex.message.toString()))
        }
    }.flowOn(Dispatchers.IO)
}