package com.example.polyscrabbleclient.game.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.polyscrabbleclient.game.domain.BoardCrawler
import com.example.polyscrabbleclient.game.sources.*
import com.example.polyscrabbleclient.lobby.sources.GameMode
import com.example.polyscrabbleclient.user.User

const val defaultTurnTime = 60

class GameModel {

    val board: BoardModel = BoardModel()

    var remainingLettersCount = mutableStateOf(0)
    var turnRemainingTime = mutableStateOf(defaultTurnTime)
    var turnTotalTime = mutableStateOf(defaultTurnTime)
    var players: MutableState<List<Player>> = mutableStateOf(listOf())
    var activePlayerIndex = mutableStateOf(0)
    val userLetters = mutableStateListOf<TileModel>()
    var isGameActive = mutableStateOf(false)
    var gameMode: MutableState<GameMode> = mutableStateOf(GameMode.Classic)
    var drawnMagicCards: MutableState<List<List<IMagicCard>>> =
        mutableStateOf(listOf(listOf(), listOf(), listOf(), listOf()))

    fun getUser(): Player? {
        return players.value.find { it.name == User.name }
    }

    fun update(gameState: GameState) {
        board.updateGrid(gameState.grid)
        updatePlayers(gameState.players)
        updateActivePlayerIndex(gameState.activePlayerIndex)
        updateRemainingLetters(gameState.lettersRemaining)
        updateEndOfGame(gameState.winnerIndex)
        updateDrawnMagicCards(gameState.drawnMagicCards)
        updateBoardCrawler()
        updateUser()
    }

    private fun updateEndOfGame(winnerIndex: ArrayList<Int>) {
        isGameActive.value = winnerIndex.isEmpty()
    }

    private fun updateBoardCrawler() {
        BoardCrawler.reset(board)
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

    private fun updateDrawnMagicCards(drawnMagicCard: ArrayList<ArrayList<IMagicCard>>) {
        drawnMagicCards.value = drawnMagicCard
    }

    private fun updateUser() {
        val updatedUser = players.value.find { player -> player.name == User.name }
        if (updatedUser === null) {
            return
        }
        userLetters.clear()
        userLetters.addAll(updatedUser.letters)
    }

    fun getPlayer(position: Int): Player? {
        return try {
            players.value[position]
        } catch (e: Exception) {
            null
        }
    }

    fun getUserIndex(): Int {
        val user = getUser() ?: return -1
        return players.value.indexOf(user)
    }

    fun getActivePlayer(): Player? {
        return getPlayer(activePlayerIndex.value)
    }

    fun createPlayersFrom(playerNames: ArrayList<String>) {
        players.value = playerNames.map { Player(it, 0) }
    }

    fun hasGameEnded(): Boolean {
        return !isGameActive.value
    }
}

data class PlayerWithIndex(
    val player: Player,
    val index: Number,
)
