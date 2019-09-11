package wen.jie.currencyconversion.utils

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import wen.jie.currencyconversion.R
import java.net.UnknownHostException

class BaseErrorHandler @JvmOverloads constructor(handlerListener: HandlerListener) {
    private val handlerListener: HandlerListener = handlerListener

    interface HandlerListener {
        fun getContext(): Context?
        fun getFragmentManager(): FragmentManager
    }

    fun handleBaseError(error: Throwable) {
        when (error) {
            is UnknownHostException -> {
                handlerListener.getContext()?.let {
                    if (!SystemHelper.isNetworkConnected(it)) {
                        showNoInternetConnectionError()
                    } else {
                        showGenericError()
                    }
                }
            }
            else -> {
                showGenericError()
            }
        }
    }

    fun handleBaseError(error: Int) {
        when (error) {
            BaseErrorHelper.ERROR_DEFAULT -> {
                showGenericError()
            }

            BaseErrorHelper.ERROR_NO_INTERNET_CONNECTION -> {
                showNoInternetConnectionError()
            }

            else -> {
                showGenericError()
            }
        }
    }

    private fun showGenericError() {
        // show general error
        // need to handle better later
        handlerListener.getContext()?.let {
            Toast.makeText(it, it.getString(R.string.general_error), Toast.LENGTH_LONG).show()
        }
    }

    private fun showNoInternetConnectionError() {
        // show no internet connection error
        //  need to handle better later
        handlerListener.getContext()?.let {
            Toast.makeText(it, it.getString(R.string.no_internet_error), Toast.LENGTH_LONG).show()
        }
    }
}