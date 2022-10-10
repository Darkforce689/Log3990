package com.example.polyscrabbleclient.game.model

import androidx.compose.runtime.mutableStateOf
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

    fun getActivePlayer(): Player? {
        return try {
            players[activePlayerIndex.value]
        } catch (e: Error) {
            null
        }
    }

    // TODO : REMOVE
    var activePlayerIndex = mutableStateOf(0)
    fun setNextActivePlayer() {
        activePlayerIndex.value = (activePlayerIndex.value + 1) % players.size
    }
}
