package com.example.polyscrabbleclient.game.model

import com.example.polyscrabbleclient.game.sources.Player

class GameModel {
    private val _players = mutableListOf<Player>()

    fun addPlayer(p: Player) {
        _players.add(p)
    }

    fun getPlayer(position: Int): Player {
        return _players[position]
    }
}
