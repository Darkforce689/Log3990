package com.example.polyscrabbleclient.game.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.polyscrabbleclient.game.sources.*
import com.example.polyscrabbleclient.message.model.User

class GameModel {

    init {
        try {
            User.updateUser()
        } catch (e: Exception) {
            println("Could not update user : $e")
        }
    }

    val board: BoardModel = BoardModel()

    var remainingLettersCount = mutableStateOf(88)
    var turnRemainingTime = mutableStateOf(14)
    var turnTotalTime = mutableStateOf(60)
    var players: MutableState<List<Player>> = mutableStateOf(listOf())
    var activePlayerIndex = mutableStateOf(0)

    fun update(gameState: GameState) {
        board.updateGrid(gameState.grid)
        updatePlayers(gameState)
        updateActivePlayerIndex(gameState)
    }

    private fun updateActivePlayerIndex(gameState: GameState) {
        activePlayerIndex.value = gameState.activePlayerIndex
    }

    private fun updatePlayers(gameState: GameState) {
        players.value = gameState.players.map { player -> Player.fromLightPlayer(player) }
    }

    fun getUser(): Player? {
        return players.value.find { player -> player.name === User.name }
    }

    fun getPlayer(position: Int): Player? {
        return try {
            players.value[position]
        } catch (e: Exception) {
            null
        }
    }

    fun getActivePlayer(): Player? {
        return getPlayer(activePlayerIndex.value)
    }

    // TODO : REMOVE
    fun setNextActivePlayer() {
        try {
            activePlayerIndex.value = (activePlayerIndex.value + 1) % players.value.size
        } catch (e: Exception) {
        }
    }
}
