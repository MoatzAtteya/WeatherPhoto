package com.example.weatherphotos.ui.main

import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherphotos.R
import com.example.weatherphotos.base.BaseAdapterItemClickListener
import com.example.weatherphotos.base.BaseFragment
import com.example.weatherphotos.databinding.FragmentMainBinding
import com.example.weatherphotos.domain.model.WeatherPhoto
import com.example.weatherphotos.helper.DataState
import com.example.weatherphotos.helper.PermissionHelper.cameraAndLocationPermission
import com.example.weatherphotos.ui.main.adapter.PhotosAdapter
import com.example.weatherphotos.ui.main.viewmodels.IMainFragmentViewModel
import com.example.weatherphotos.ui.main.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainFragment : BaseFragment<IMainFragmentViewModel, FragmentMainBinding>() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val adapter : PhotosAdapter by lazy {
        PhotosAdapter()
    }


    override fun initView() {
        baseViewBinding.openCameraBtn.setOnClickListener {
            checkCameraStoragePermission()
        }
        viewModel?.getWeatherPhotos()
    }

    override fun getContentView() = R.layout.fragment_main

    override fun getSnackBarAnchorView() = baseViewBinding.root

    override fun subscribeObservers() {
        getPhotosResponse()
    }

    private fun getPhotosResponse() {
        viewModel?.weatherPhotosResponse()?.observe(this) { dataState ->
            when (dataState) {
                is DataState.Data -> {
                    val photosList = dataState.data
                    if (photosList != null)
                        initPhotosRv(photosList)
                }
                is DataState.Error -> {
                    showDBError()
                }
                is DataState.Loading -> {}
            }
        }
    }

    private fun initPhotosRv(photosList: List<WeatherPhoto>) {
        if (photosList.isEmpty()) {
            baseViewBinding.emptyPhotosMsg.visibility = View.VISIBLE
            return
        }
        baseViewBinding.emptyPhotosMsg.visibility = View.GONE
        adapter.differ.submitList(photosList)
        adapter.submitClickCallback(object : BaseAdapterItemClickListener<WeatherPhoto> {
            override fun onItemClicked(position: Int, itemModel: WeatherPhoto) {

            }
        })
        baseViewBinding.photosRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        baseViewBinding.photosRv.adapter = adapter
        handleSwipeArticleListener()
    }

    private fun handleSwipeArticleListener() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                viewModel?.deletePhoto(adapter.differ.currentList[position])
                // deleting the deleted photo from device.
                try {
                    File(adapter.differ.currentList[position].path).delete()
                    showInfoSnackBar(getString(R.string.photo_deleted_msg))
                }catch (ex : Exception){
                    ex.printStackTrace()
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(baseViewBinding.photosRv)
        }
    }

    override fun initializeViewModel() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    private fun checkCameraStoragePermission() {
        requestPermissionLauncher.launch(cameraAndLocationPermission)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted ->
            if (isGranted.containsValue(false)) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.enable_permissions_msg),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                openCameraFragment()
            }
        }

    private fun openCameraFragment() {
        findNavController().navigate(MainFragmentDirections.actionMainFragmentToPhotoPrepareFragment())
    }


}