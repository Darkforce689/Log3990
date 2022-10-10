package com.example.polyscrabbleclient.game.model

import com.example.polyscrabbleclient.game.sources.Player

class GameModel {
    val players = mutableListOf<Player>()

    fun addPlayer(p: Player) {
        players.add(p)
    }

    fun getPlayer(position: Int): Player {
        return players[position]
    }

    init {
        addPlayer(Player("playername1"))
        addPlayer(Player("playername2"))
        addPlayer(Player("playername3"))
        addPlayer(Player("playername4"))
    }

    fun getActivePlayer(): Player {
        return players[2]
    }
}
