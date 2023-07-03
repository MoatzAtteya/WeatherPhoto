package com.example.weatherphotos

import android.Manifest

object PermissionHelper {
    val cameraAndLocationPermission =
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA
        )

}