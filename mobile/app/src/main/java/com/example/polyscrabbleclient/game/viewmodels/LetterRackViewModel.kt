package com.example.polyscrabbleclient.game.viewmodels

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.game.domain.TileCreator
import com.example.polyscrabbleclient.game.model.TileModel


class LetterRackViewModel : ViewModel()
{
    private val tileCreator = TileCreator();
    lateinit var tiles:List<TileModel>

    fun updateLetters(letters: List<Char>) {
        tiles = letters.map { letter -> tileCreator.createTileFromLetter(letter) }
    }
}
