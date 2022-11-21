package com.example.polyscrabbleclient

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.utils.PhysicalButtons

class ChatPageViewModel: ViewModel() {
    private var isBackPressed = false
    fun onCloseChat() {
        if (isBackPressed) {
            return
        }
        PhysicalButtons.popBackPress()
        isBackPressed = false
    }

    fun onBackPressed() {
        isBackPressed = true
    }

    fun getIsBackPressed(): Boolean {
        return isBackPressed
    }
}
