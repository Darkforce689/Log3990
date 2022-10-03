package com.example.polyscrabbleclient

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.auth.AppSocketHandler

class StartViewModel : ViewModel() {
    fun connectAppSocket() {
        val thread = Thread {
            AppSocketHandler.setSocket()
            AppSocketHandler.connect()
        }
        thread.start()
        thread.join()
    }

    fun disconnectAppSocket() {
        Thread {
            AppSocketHandler.disconnect()
        }.start()
    }
}
