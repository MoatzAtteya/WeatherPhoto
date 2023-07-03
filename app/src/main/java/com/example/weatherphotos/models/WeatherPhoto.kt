package com.example.weatherphotos.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "WeatherPhotos"
)
data class WeatherPhoto(
    @PrimaryKey(autoGenerate = true)
    val id : Int?= null,
    val path : String
)
