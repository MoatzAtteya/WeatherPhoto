package com.example.weatherphotos.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherphotos.PermissionHelper.cameraAndLocationPermission
import com.example.weatherphotos.R
import com.example.weatherphotos.databinding.FragmentMainBinding
import com.example.weatherphotos.ui.photo_prepare.PhotoPrepareFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: FragmentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.openCameraBtn.setOnClickListener {
            checkCameraStoragePermission()
        }

        return binding.root
    }


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted ->
            if (isGranted.containsValue(false)) {
                Toast.makeText(
                    requireContext(),
                    " You need to enable the permissions ",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                updateUi()
            }
        }

    private fun updateUi() {
        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.container,PhotoPrepareFragment.newInstance()).commit()
    }

    private fun checkCameraStoragePermission() {
        requestPermissionLauncher.launch(cameraAndLocationPermission)
    }
}