package com.example.polyscrabbleclient.game.viewmodels

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.game.sources.Player

class GameViewModel : ViewModel() {
    private val _players = mutableListOf<Player>()

    fun addPlayer(p: Player) {
        _players.add(p)
    }
}
