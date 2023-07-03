package com.example.weatherphotos.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.weatherphotos.models.WeatherPhoto
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert
    suspend fun insertWeatherPhoto(weatherPhoto: WeatherPhoto) : Long

    @Query("SELECT * FROM WeatherPhotos")
    fun getWeatherPhotos() : Flow<List<WeatherPhoto>>

}