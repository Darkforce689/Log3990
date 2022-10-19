package com.example.polyscrabbleclient.game.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.polyscrabbleclient.game.sources.*
import com.example.polyscrabbleclient.message.model.User
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

const val defaultTurnTime = 60

@OptIn(DelicateCoroutinesApi::class)
class GameModel {

    init {
        try {
            GlobalScope.launch() {
                // TODO : FIND A WAY TO GET USER NAME
                // val updateThread = User.updateUser()
                // username = User.name
                // updateThread.start()
                // updateThread.join()
            }
        } catch (e: Exception) {
            println("Could not update user : $e")
        }
    }

    val board: BoardModel = BoardModel()

    var remainingLettersCount = mutableStateOf(0)
    var turnRemainingTime = mutableStateOf(defaultTurnTime)
    var turnTotalTime = mutableStateOf(defaultTurnTime)
    var players: MutableState<List<Player>> = mutableStateOf(listOf())
    var activePlayerIndex = mutableStateOf(0)
    var user: MutableState<Player?> = mutableStateOf(null)
    var username: String? = null

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
        user.value = players.value.find { player -> player.name == "helloFrom2015" }
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
