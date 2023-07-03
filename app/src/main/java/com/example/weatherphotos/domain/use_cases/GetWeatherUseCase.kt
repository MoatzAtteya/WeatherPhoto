package com.example.weatherphotos.domain.use_cases

import com.example.weatherphotos.DataState
import com.example.weatherphotos.ErrorCode
import com.example.weatherphotos.ProgressBarState
import com.example.weatherphotos.ResponseCodeHandler
import com.example.weatherphotos.data.remote.WeatherRepository
import com.example.weatherphotos.models.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository)
{
    fun invoke(lat:Double ,long: Double): Flow<DataState<WeatherResponse>> = flow {
        try {
            emit(DataState.Loading(ProgressBarState.Loading))
            val data = repository.getCurrentWeather(
                lat,long
            )
            val responseType = ErrorCode.getCodeStatus(data.code())

            emit(DataState.Loading(ProgressBarState.Idle))
            if (responseType == ResponseCodeHandler.SUCCESSFUL) {
                emit(DataState.Data(data.body()))
            } else
                emit(DataState.Error(responseType, responseType.name))

        } catch (ex: Exception) {
            ex.printStackTrace()
            emit(DataState.Loading(ProgressBarState.Idle))
            emit(DataState.Error(ResponseCodeHandler.SERVER_ERROR, ex.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

}