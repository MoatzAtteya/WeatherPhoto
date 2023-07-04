package com.example.weatherphotos.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "WeatherPhotos"
)
data class WeatherPhoto(
    @PrimaryKey(autoGenerate = true)
    val id : Int?= null,
    val path : String,
    val location : String,
    val temp : String,
    val creationDate : String
)
