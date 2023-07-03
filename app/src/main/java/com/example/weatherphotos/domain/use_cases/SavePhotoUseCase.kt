package com.example.weatherphotos.domain.use_cases

import com.example.weatherphotos.DataState
import com.example.weatherphotos.ErrorCode
import com.example.weatherphotos.ProgressBarState
import com.example.weatherphotos.ResponseCodeHandler
import com.example.weatherphotos.data.dao.WeatherDBRepository
import com.example.weatherphotos.data.remote.WeatherRepository
import com.example.weatherphotos.models.WeatherPhoto
import com.example.weatherphotos.models.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SavePhotoUseCase @Inject constructor(
    private val repository: WeatherDBRepository)
{
    fun invoke(weatherPhoto: WeatherPhoto): Flow<DataState<Long>> = flow {
        try {
            emit(DataState.Loading(ProgressBarState.Loading))
            val data = repository.saveWeatherPhoto(
                weatherPhoto
            )
            emit(DataState.Loading(ProgressBarState.Idle))
            emit(DataState.Data(data))

        } catch (ex: Exception) {
            ex.printStackTrace()
            emit(DataState.Loading(ProgressBarState.Idle))
            emit(DataState.Error(ResponseCodeHandler.SERVER_ERROR, ex.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

}