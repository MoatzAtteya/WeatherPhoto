package com.example.weatherphotos.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import com.example.weatherphotos.models.WeatherPhoto

@Database(entities = [WeatherPhoto::class], version = 1, exportSchema = false)
abstract class WeatherDataBase : RoomDatabase() {
    abstract fun weatherDao() : WeatherDao
}