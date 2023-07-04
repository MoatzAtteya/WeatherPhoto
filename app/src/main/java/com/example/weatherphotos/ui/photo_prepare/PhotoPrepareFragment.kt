package com.example.weatherphotos.ui.photo_prepare

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.weatherphotos.DomainConstants
import com.example.weatherphotos.R
import com.example.weatherphotos.base.BaseFragment
import com.example.weatherphotos.databinding.FragmentPhotoPrepareBinding
import com.example.weatherphotos.domain.model.WeatherPhoto
import com.example.weatherphotos.domain.model.WeatherResponse
import com.example.weatherphotos.helper.DataState
import com.example.weatherphotos.helper.FileUtils
import com.example.weatherphotos.helper.ProgressBarState
import com.example.weatherphotos.ui.photo_prepare.viewmodels.IPhotoPrepareViewModel
import com.example.weatherphotos.ui.photo_prepare.viewmodels.PhotoPrepareViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.guhungry.photomanipulator.factory.AndroidConcreteFactory
import com.guhungry.photomanipulator.factory.AndroidFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class PhotoPrepareFragment : BaseFragment<IPhotoPrepareViewModel , FragmentPhotoPrepareBinding >() {
    private var imageUri: Uri? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var thumbnail: Bitmap? = null
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun getContentView() = R.layout.fragment_photo_prepare

    override fun getSnackBarAnchorView() = baseViewBinding.root

    override fun initializeViewModel() {
        viewModel = ViewModelProvider(this)[PhotoPrepareViewModel::class.java]
    }

    override fun initView() {
        openCamera()
        baseViewBinding.facebookShare.setOnClickListener {
            shareImg(DomainConstants.FACEBOOK_PACKAGE)
        }
        baseViewBinding.twitterShare.setOnClickListener {
            shareImg(DomainConstants.TWITTER_PACKAGE)
        }
    }

    private fun shareImg(application: String) {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "image/JPEG"
        share.putExtra(Intent.EXTRA_STREAM, FileUtils.getImageUri(requireContext(), thumbnail!!))
        share.setPackage(application)
        startActivity(Intent.createChooser(share, "Share image using"))
    }


    override fun subscribeObservers() {
        getWeatherStatusResponse()
    }

    private fun getWeatherStatusResponse() {
        lifecycleScope.launch {
            var isComplete = false
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                if (!isComplete) {
                    viewModel?.weatherStatusResponse()?.collect { dataState ->
                        when (dataState) {
                            is DataState.Data -> {
                                val weatherResponse = dataState.data!!
                                updateImageUi(weatherResponse)
                            }
                            is DataState.Error -> {
                                showApiError(dataState.exception!!)
                            }
                            is DataState.Loading -> {
                                if (dataState.progressBarState == ProgressBarState.Loading)
                                    baseViewBinding.progressBar.visibility = View.VISIBLE
                                else
                                    baseViewBinding.progressBar.visibility = View.GONE
                            }
                        }
                    }
                }
            }
            isComplete = true
        }
    }

    private fun updateImageUi(weatherResponse: WeatherResponse) {
        baseViewBinding.twitterShare.visibility = View.VISIBLE
        baseViewBinding.facebookShare.visibility = View.VISIBLE
        val location = buildString {
            append("Location: ")
            append(weatherResponse.name)
        }
        val temp = buildString {
            append("Temperature: ")
            append("${weatherResponse.main.temp} c")
        }
        val status = buildString {
            append("Status: ")
            append(weatherResponse.weather[0].description)
        }
        printText(
            thumbnail!!, location, PointF(450F, 200F), Color.parseColor("#000000"),
            110F, Paint.Align.CENTER, 5F
        )
        printText(
            thumbnail!!, temp, PointF(560F, 330F), Color.parseColor("#000000"),
            110F, Paint.Align.CENTER, 5F
        )
        printText(
            thumbnail!!, status, PointF(450F, 450F), Color.parseColor("#000000"),
            110F, Paint.Align.CENTER, 5F
        )
        Glide.with(requireContext()).load(thumbnail).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                saveWeatherPhoto(weatherResponse)
                return false
            }
        }).into(baseViewBinding.weatherPhotoIv)
    }

    private fun saveWeatherPhoto(weatherResponse: WeatherResponse) {
        val file = FileUtils.saveWeatherPhoto(requireContext(),thumbnail!!)
        viewModel?.saveWeatherPhoto(
            WeatherPhoto(
                path = file.path,
                location = weatherResponse.name,
                temp = weatherResponse.main.temp.toInt().toString(),
                creationDate = FileUtils.getCurrentDateTime()
            )
        )
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
                    ).copy(Bitmap.Config.ARGB_8888, true)

                    imageUrl = FileUtils.getRealPathFromURI(imageUri!!,requireContext())
                    getLastKnownLocation()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                findNavController().navigateUp()
            }
        }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    longitude = location.longitude
                    latitude = location.latitude
                    viewModel?.getWeatherStatus(latitude, longitude)
                }
            }
    }

    private fun printText(
        image: Bitmap,
        text: String,
        position: PointF,
        color: Int,
        size: Float,
        alignment: Paint.Align = Paint.Align.LEFT,
        thickness: Float = 0f,
        factory: AndroidFactory = AndroidConcreteFactory()
    ) {
        val canvas = factory.makeCanvas(image)
        val paint = factory.makePaint().apply {
            this.color = color
            textSize = size
            textSize
            textAlign = alignment

            if (thickness > 0) {
                style = Paint.Style.FILL_AND_STROKE
                strokeWidth = thickness
            }
        }
        canvas.drawText(text, position.x, position.y, paint)
    }

    companion object {
        fun newInstance() = PhotoPrepareFragment()
    }
}