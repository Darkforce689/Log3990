package com.example.polyscrabbleclient.utils


object PhysicalButtons {
    var backPressed: (() -> Unit)? = null

    fun reset() {
        backPressed = null
    }
}
