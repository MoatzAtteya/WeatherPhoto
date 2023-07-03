package com.example.weatherphotos

object ErrorCode {
    fun getCodeStatus(code: Int?): ResponseCodeHandler{
        return when (code) {
            in 200..300 -> {
                ResponseCodeHandler.SUCCESSFUL
            }
            401 -> {
                ResponseCodeHandler.UNAUTHORIZED
            }
            403 -> {
                ResponseCodeHandler.FORBIDDEN
            }
            else -> {
                ResponseCodeHandler.SERVER_ERROR
            }
        }
    }
}

enum class ResponseCodeHandler{
    UNAUTHORIZED,
    SERVER_ERROR,
    FORBIDDEN,
    SUCCESSFUL
}