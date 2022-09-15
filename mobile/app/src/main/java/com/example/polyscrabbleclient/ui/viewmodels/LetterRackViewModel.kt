package com.example.polyscrabbleclient.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.data.sources.Player

class LetterRackViewModel : ViewModel() {
    private val player = Player()
    val letters = player.letters
}
