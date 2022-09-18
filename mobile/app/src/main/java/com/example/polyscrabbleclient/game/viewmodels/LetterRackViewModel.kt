package com.example.polyscrabbleclient.game.viewmodels

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.game.sources.Player

class LetterRackViewModel : ViewModel() {
    private val player = Player()
    val letters = player.letters
}
