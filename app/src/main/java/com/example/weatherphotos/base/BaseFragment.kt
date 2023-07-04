package com.example.weatherphotos.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.example.weatherphotos.R
import com.example.weatherphotos.helper.ResponseCodeHandler
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment<VM : IBaseViewModel , VB : ViewDataBinding> : Fragment() {

    protected var viewModel: VM? = null

    lateinit var baseViewBinding : VB
    protected abstract fun initView()
    protected abstract fun getContentView(): Int
    protected open fun initializeViewModel(){}
    protected open fun subscribeObservers(){}
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        baseViewBinding = DataBindingUtil.inflate(inflater , getContentView() , container , false)
        return baseViewBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        initView()
        subscribeObservers()
    }

    abstract fun getSnackBarAnchorView(): View


    private fun showInfoSnackBar(@StringRes message: Int): Snackbar {
        val snackbar = Snackbar.make(getSnackBarAnchorView(), message, Snackbar.LENGTH_LONG)
        snackbar.show()
        return snackbar
    }
    fun showInfoSnackBar( message: String): Snackbar {
        val snackbar = Snackbar.make(getSnackBarAnchorView(), message, Snackbar.LENGTH_LONG)
        snackbar.show()
        return snackbar
    }

    fun showApiError(exception: ResponseCodeHandler){
        when (exception) {
            ResponseCodeHandler.UNAUTHORIZED -> showInfoSnackBar(R.string.msg_unauthorize_error)
            ResponseCodeHandler.FORBIDDEN -> showInfoSnackBar(R.string.msg_general_error)
            else -> {
                showInfoSnackBar(R.string.msg_general_error)
            }
        }
    }

    fun showDBError(){
        showInfoSnackBar(getString(R.string.database_error_msg))
    }

}
