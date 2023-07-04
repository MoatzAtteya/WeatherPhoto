package com.example.weatherphotos.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherphotos.domain.model.WeatherPhoto

@Database(entities = [WeatherPhoto::class], version = 1, exportSchema = false)
abstract class WeatherDataBase : RoomDatabase() {
    abstract fun weatherDao() : WeatherDao
}