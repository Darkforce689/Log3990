package com.example.polyscrabbleclient.game.model

import androidx.compose.runtime.mutableStateOf
import com.example.polyscrabbleclient.game.sources.GameState
import com.example.polyscrabbleclient.game.sources.Player
import com.example.polyscrabbleclient.game.sources.RemainingTime
import com.example.polyscrabbleclient.game.sources.TransitionGameState

class GameModel {
    val board: BoardModel = BoardModel()

    var remainingLettersCount = mutableStateOf(88)
    var turnRemainingTime = mutableStateOf(14)
    var turnTotalTime = mutableStateOf(60)

    val players = mutableListOf<Player>()

    fun update(newGameState: GameState) {
        board.updateGrid(newGameState.grid)
        // TODO : UPDATE OTHER FIELDS
    }

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
