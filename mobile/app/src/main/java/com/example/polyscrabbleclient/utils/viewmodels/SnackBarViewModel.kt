package com.example.polyscrabbleclient.utils.viewmodels

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.utils.SnackBarConfig
import com.example.polyscrabbleclient.utils.SnackBarInvoker

class SnackBarViewModel: ViewModel() {
    init {
        SnackBarInvoker.subscribe {
            openSnackBar(it)
        }
    }

    private fun openSnackBar(snackBarConfig: SnackBarConfig) {
        openSnackBarCallback(snackBarConfig)
    }

    private var openSnackBarCallback: (snackBarConfig: SnackBarConfig) -> Unit = {}

    fun setOpenCallback(callback: (snackBarConfig: SnackBarConfig) -> Unit) {
        openSnackBarCallback = callback
    }
}
