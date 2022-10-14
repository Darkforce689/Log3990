package com.example.polyscrabbleclient.game.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.polyscrabbleclient.game.domain.TileCreator
import com.example.polyscrabbleclient.game.sources.*
import com.example.polyscrabbleclient.message.model.User
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class GameModel {

    init {
        try {
            GlobalScope.launch() {
                val updateThread = User.updateUser()
                // TODO : FIND A WAY TO GET USER NAME
                username = User.name
                updateThread.start()
                updateThread.join()
            }
            println("Username : ${User.name}")
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
    var user: MutableState<Player?> = mutableStateOf(null)
    var username: String? = null

    fun update(gameState: GameState) {
        board.updateGrid(gameState.grid)
        updatePlayers(gameState)
        updateActivePlayerIndex(gameState)
        updateUser()
    }

    private fun updateActivePlayerIndex(gameState: GameState) {
        activePlayerIndex.value = gameState.activePlayerIndex
    }

    private fun updatePlayers(gameState: GameState) {
        players.value = gameState.players.map { player -> Player.fromLightPlayer(player) }
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

    // TODO : REMOVE
    fun setNextActivePlayer() {
        try {
            activePlayerIndex.value = (activePlayerIndex.value + 1) % players.value.size
        } catch (e: Exception) {
        }
    }
}
