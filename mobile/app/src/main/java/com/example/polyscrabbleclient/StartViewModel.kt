package com.example.polyscrabbleclient

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.auth.AppSocketHandler

class StartViewModel : ViewModel() {
    fun disconnectAppSocket() {
        Thread {
            AppSocketHandler.disconnect()
        }.start()
    }
}

