package com.example.weatherphotos.helper

import android.content.Context
import android.content.ContextWrapper
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.example.weatherphotos.domain.model.WeatherResponse
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {

     fun saveWeatherPhoto( context: Context , thumbnail : Bitmap): File {
        val wrapper = ContextWrapper(context)
        var file = wrapper.getDir("Weather_photos", Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")
        val stream: OutputStream = FileOutputStream(file)
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        stream.flush()
        stream.close()
        return file
    }

    fun getRealPathFromURI(contentUri: Uri , context: Context): String? {
        var res: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = context.contentResolver.query(contentUri, proj, null, null, null)
        if (cursor!!.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(column_index)
        }
        cursor.close()
        return res
    }

     fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Photo",
            null
        )
        return Uri.parse(path)
    }

     fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm" , Locale.ENGLISH)
        return sdf.format(Date())
    }
}