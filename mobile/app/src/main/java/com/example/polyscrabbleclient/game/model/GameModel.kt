package com.example.polyscrabbleclient.game.model

import androidx.compose.runtime.mutableStateOf
import com.example.polyscrabbleclient.game.sources.GameState
import com.example.polyscrabbleclient.game.sources.Player
import com.example.polyscrabbleclient.game.sources.RemainingTime
import com.example.polyscrabbleclient.game.sources.TransitionGameState

class GameModel {
    val startTime = mutableStateOf<GameState?>(null)
    val remainingTime = mutableStateOf<RemainingTime?>(null)
    val gameState = mutableStateOf<GameState?>(null)
    val transitionGameState = mutableStateOf<TransitionGameState?>(null)

    val board: BoardModel = BoardModel()

    val players = mutableListOf<Player>()

    fun addPlayer(p: Player) {
        players.add(p)
    }

    fun getPlayer(position: Int): Player {
        return players[position]
    }

    fun getActivePlayer(): Player? {
        return try {
            players[activePlayerIndex.value]
        } catch (e: Exception) {
            null
        }
    }

    // TODO : REMOVE
    var activePlayerIndex = mutableStateOf(0)
    fun setNextActivePlayer() {
        try {
            activePlayerIndex.value = (activePlayerIndex.value + 1) % players.size
        } catch (e: Exception) {
        }
    }
}
