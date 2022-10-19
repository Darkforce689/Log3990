package com.example.polyscrabbleclient.game.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.polyscrabbleclient.game.domain.TileCreator
import com.example.polyscrabbleclient.game.sources.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

const val defaultTurnTime = 60

class GameModel {

    val board: BoardModel = BoardModel()

    var remainingLettersCount = mutableStateOf(0)
    var turnRemainingTime = mutableStateOf(defaultTurnTime)
    var turnTotalTime = mutableStateOf(defaultTurnTime)
    var players: MutableState<List<Player>> = mutableStateOf(listOf())
    var activePlayerIndex = mutableStateOf(0)
    // TODO : FIND A WAY TO GET USER NAME
    var username: String? = "helloFrom2015"
    var user: MutableState<Player> = mutableStateOf(
        Player(
            "fake_$username",
            0,
        )
    )


    fun update(gameState: GameState) {
        board.updateGrid(gameState.grid)
        updatePlayers(gameState.players)
        updateActivePlayerIndex(gameState.activePlayerIndex)
        updateRemainingLetters(gameState.lettersRemaining)
        updateUser()
    }

    private fun updateRemainingLetters(updatedLettersRemaining: Int) {
        remainingLettersCount.value = updatedLettersRemaining
    }

    private fun updateActivePlayerIndex(updatedActivePlayerIndex: Int) {
        activePlayerIndex.value = updatedActivePlayerIndex
    }

    private fun updatePlayers(updatedPlayers: ArrayList<LightPlayer>) {
        players.value = updatedPlayers.map { player -> Player.fromLightPlayer(player) }
    }

    private fun updateUser() {
        // TODO : USE CORRECT USERNAME
        val updatedUser = players.value.find { player -> player.name == username }
        if (updatedUser === null) {
            return
        }
        user.value = updatedUser
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
}
