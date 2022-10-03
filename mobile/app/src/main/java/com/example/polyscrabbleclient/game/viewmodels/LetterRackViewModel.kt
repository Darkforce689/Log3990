package com.example.polyscrabbleclient.game.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.game.domain.LetterCreator
import com.example.polyscrabbleclient.game.sources.Player



class LetterRackViewModel : ViewModel() {
    private val letterCreator = LetterCreator();
    private val player = Player()
    val tiles = player.letters.map { letter -> letterCreator.createTileFromLetter(letter) }
}
