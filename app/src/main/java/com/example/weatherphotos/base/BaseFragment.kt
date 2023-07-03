package com.example.weatherphotos.base

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.weatherphotos.R
import com.example.weatherphotos.ResponseCodeHandler
import com.google.android.material.snackbar.Snackbar
import java.util.*

abstract class BaseFragment<VM : IBaseViewModel> : Fragment() {

    protected var viewModel: VM? = null

//    lateinit var baseViewBinding : VB
    protected abstract fun initView()
    protected abstract fun getContentView(): Int
    protected open fun initializeViewModel(){}
    protected open fun subscribeObservers(){}
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        baseViewBinding = DataBindingUtil.inflate(inflater , getContentView() , container , false)
//        return baseViewBinding.root
//    }

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


    fun showInfoSnackBar(@StringRes message: Int): Snackbar {
        val snackbar = Snackbar.make(getSnackBarAnchorView(), message, Snackbar.LENGTH_LONG)
        snackbar.show()
        return snackbar
    }

    fun showLoadingDialog(context: Context?) {

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

}
