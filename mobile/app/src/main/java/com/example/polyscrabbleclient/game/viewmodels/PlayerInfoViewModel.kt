package com.example.polyscrabbleclient.game.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.utils.constants.NoAvatar

class PlayerInfoViewModel : ViewModel() {
    val avatar = mutableStateOf(NoAvatar)
}
