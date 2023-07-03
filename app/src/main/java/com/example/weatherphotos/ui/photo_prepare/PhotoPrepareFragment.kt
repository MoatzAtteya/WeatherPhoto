package com.example.weatherphotos.ui.photo_prepare

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.drawable.Drawable
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
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.weatherphotos.DataState
import com.example.weatherphotos.DomainConstants
import com.example.weatherphotos.ProgressBarState
import com.example.weatherphotos.R
import com.example.weatherphotos.base.BaseFragment
import com.example.weatherphotos.databinding.FragmentPhotoPrepareBinding
import com.example.weatherphotos.models.WeatherPhoto
import com.example.weatherphotos.models.WeatherResponse
import com.example.weatherphotos.ui.photo_prepare.viewmodels.IPhotoPrepareViewModel
import com.example.weatherphotos.ui.photo_prepare.viewmodels.PhotoPrepareViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.guhungry.photomanipulator.factory.AndroidConcreteFactory
import com.guhungry.photomanipulator.factory.AndroidFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*


@AndroidEntryPoint
class PhotoPrepareFragment : BaseFragment<IPhotoPrepareViewModel >() {
    private lateinit var binding: FragmentPhotoPrepareBinding
    private var imageUri: Uri? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var thumbnail : Bitmap ?= null
    private var imageUrl :String?= null
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
        binding.facebookShare.setOnClickListener {
            shareImg(DomainConstants.FACEBOOK_PACKAGE)
        }
        binding.twitterShare.setOnClickListener {
            shareImg(DomainConstants.TWITTER_PACKAGE)
        }
    }

    private fun shareImg(application : String){
        val share = Intent(Intent.ACTION_SEND)
        share.type = "image/JPEG"
        share.putExtra(Intent.EXTRA_STREAM, getImageUri(requireContext(),thumbnail!!))
        share.setPackage(application)
        startActivity(Intent.createChooser(share, "Share image using"))
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
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

    override fun subscribeObservers() {
        getWeatherStatusResponse()
    }

    private fun getWeatherStatusResponse() {
        lifecycleScope.launch {
            var isComplete = false
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                if (!isComplete) {
                    viewModel?.weatherStatusResponse()?.collect{dataState->
                        when(dataState){
                            is DataState.Data -> {
                                val weatherResponse = dataState.data!!
                                updateImageUi(weatherResponse)
                            }
                            is DataState.Error -> {
                                showApiError(dataState.exception!!)
                            }
                            is DataState.Loading -> {
                                if (dataState.progressBarState == ProgressBarState.Loading)
                                    binding.progressBar.visibility = View.VISIBLE
                                else
                                    binding.progressBar.visibility = View.GONE
                            }
                        }
                    }
                }
            }
            isComplete = true
        }
    }

    private fun updateImageUi(weatherResponse: WeatherResponse) {
        binding.twitterShare.visibility = View.VISIBLE
        binding.facebookShare.visibility = View.VISIBLE
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
        printText(thumbnail!!,location, PointF(400F, 200F),Color.parseColor("#000000"),
            110F,Paint.Align.CENTER,5F)
        printText(thumbnail!!,temp, PointF(540F, 330F),Color.parseColor("#000000"),
            110F,Paint.Align.CENTER,5F)
        printText(thumbnail!!,status, PointF(450F, 450F),Color.parseColor("#000000"),
            110F,Paint.Align.CENTER,5F)
        Glide.with(requireContext()).load(thumbnail).listener(object : RequestListener<Drawable>{
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
                saveWeatherPhoto()
                return false
            }
        }).into(binding.weatherPhotoIv)
    }

    private fun saveWeatherPhoto() {
        val wrapper = ContextWrapper(requireContext())
        var file = wrapper.getDir("Weather_photos", Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")
        val stream: OutputStream = FileOutputStream(file)
        thumbnail!!.compress(Bitmap.CompressFormat.JPEG,80,stream)
        stream.flush()
        stream.close()
        Log.d("saveWeatherPhoto", file.path)
        viewModel?.saveWeatherPhoto(WeatherPhoto(path = file.path))
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

                    imageUrl = getRealPathFromURI(imageUri)
                    getLastKnownLocation()
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
                }
            }
    }

    private fun printText(image: Bitmap, text: String, position: PointF, color: Int, size: Float, alignment: Paint.Align = Paint.Align.LEFT, thickness: Float = 0f, factory: AndroidFactory = AndroidConcreteFactory()) {
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
        canvas.drawText(text, position.x, position.y , paint)
    }

    companion object {
        fun newInstance() = PhotoPrepareFragment()
    }
}