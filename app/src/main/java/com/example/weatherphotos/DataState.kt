package com.example.weatherphotos

sealed class DataState<T> {

    data class Error<T>(
        val exception: ResponseCodeHandler? = null,
        val message: String
    ) : DataState<T>()

    data class Data<T>(
        val data: T? = null,
        val otherInfo: Any? = null
    ) : DataState<T>()

    data class Loading<T>(
        val progressBarState: ProgressBarState = ProgressBarState.Idle
    ) : DataState<T>()
}


sealed class ProgressBarState {
    object Loading : ProgressBarState()
    object Idle : ProgressBarState()
}