package com.example.weatherphotos.ui.photo_prepare

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.example.weatherphotos.DataState
import com.example.weatherphotos.R
import com.example.weatherphotos.base.BaseFragment
import com.example.weatherphotos.databinding.FragmentPhotoPrepareBinding
import com.example.weatherphotos.models.WeatherResponse
import com.example.weatherphotos.ui.photo_prepare.viewmodels.IPhotoPrepareViewModel
import com.example.weatherphotos.ui.photo_prepare.viewmodels.PhotoPrepareViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.guhungry.photomanipulator.BitmapUtils
import com.guhungry.photomanipulator.factory.AndroidConcreteFactory
import com.guhungry.photomanipulator.factory.AndroidFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PhotoPrepareFragment : BaseFragment<IPhotoPrepareViewModel >() {


    private lateinit var binding: FragmentPhotoPrepareBinding
    private var imageUri: Uri? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var thumbnail : Bitmap ?= null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotoPrepareBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }
    override fun getContentView() = R.layout.fragment_photo_prepare

    override fun getSnackBarAnchorView() = binding.root

    override fun initializeViewModel() {
        viewModel = ViewModelProvider(this)[PhotoPrepareViewModel::class.java]
    }

    override fun initView() {
        openCamera()
    }



    override fun subscribeObservers() {
        getWeatherStatusResponse()
    }

    private fun getWeatherStatusResponse() {
        lifecycleScope.launch {
            var isComplete = false
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                if (!isComplete) {
                    viewModel?.weatherStatusResponse()?.collect{dataState->
                        when(dataState){
                            is DataState.Data -> {
                                val weatherResponse = dataState.data!!
                                updateImageUi(weatherResponse)
                                Log.d("getWeatherStatusResponse", "$weatherResponse")
                            }
                            is DataState.Error -> {
                                showApiError(dataState.exception!!)
                            }
                            is DataState.Loading -> {

                            }
                        }
                    }
                }
            }
            isComplete = true
        }
    }

    private fun updateImageUi(weatherResponse: WeatherResponse) {
        val centreX=binding.weatherPhotoIv.pivotX  / 2
        val centreY=binding.weatherPhotoIv.pivotY / 2
        val weatherInfo = buildString {
            append("${weatherResponse.name } \n ")
            append("${weatherResponse.weather[0].description } \n ")
            append("${weatherResponse.main.temp} c")
        }
        printText(thumbnail!!,weatherInfo, PointF(centreX, centreY),Color.parseColor("#000000"),
            200F,Paint.Align.CENTER,5F)
        Glide.with(requireContext()).load(thumbnail).into(binding.weatherPhotoIv)
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
        imageUri = requireActivity().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        imgLauncher.launch(intent)
    }


    private val imgLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                try {
                     thumbnail = MediaStore.Images.Media.getBitmap(
                        requireActivity().contentResolver, imageUri
                    ).copy(Bitmap.Config.ARGB_8888,true)

                    val imageUrl = getRealPathFromURI(imageUri)
                    getLastKnownLocation()
                    Log.d("WeatherPhoto path", "$imageUrl")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                requireActivity().onBackPressed()
            }
        }

    private fun getRealPathFromURI(contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = requireActivity().managedQuery(contentUri, proj, null, null, null)
        val columnIndex: Int = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(columnIndex)
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    longitude = location.longitude
                    latitude = location.latitude
                    viewModel?.getWeatherStatus(latitude,longitude)
                    Log.e("MainFragment", "coordinates: $longitude $latitude", )
                }
            }
    }

    private fun printText(image: Bitmap, text: String, position: PointF, color: Int, size: Float, alignment: Paint.Align = Paint.Align.LEFT, thickness: Float = 0f, factory: AndroidFactory = AndroidConcreteFactory()) {
        val canvas = factory.makeCanvas(image)

        val paint = factory.makePaint().apply {
            this.color = color
            textSize = size
            textAlign = alignment

            if (thickness > 0) {
                style = Paint.Style.FILL_AND_STROKE
                strokeWidth = thickness
            }
        }
        canvas.drawText(text, position.x, position.y , paint)
    }

    companion object {
        fun newInstance() = PhotoPrepareFragment()
    }
}