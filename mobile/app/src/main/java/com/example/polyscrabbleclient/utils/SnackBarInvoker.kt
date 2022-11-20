package com.example.polyscrabbleclient.utils

import androidx.compose.material.SnackbarDuration

data class SnackBarConfig(
    val message: String,
    val action: String,
    val duration: SnackbarDuration,
)

object SnackBarInvoker {
    fun invoke(config: SnackBarConfig) {
        observers.forEach {
            it(config)
        }
    }

    private val observers: MutableList<(SnackBarConfig) -> Unit> = ArrayList()
    fun subscribe(callback: (SnackBarConfig) -> Unit) {
        observers.add(callback)
    }
}
