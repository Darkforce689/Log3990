package com.example.polyscrabbleclient.game.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.polyscrabbleclient.game.domain.BoardCrawler
import com.example.polyscrabbleclient.game.sources.GameState
import com.example.polyscrabbleclient.game.sources.IMagicCard
import com.example.polyscrabbleclient.game.sources.LightPlayer
import com.example.polyscrabbleclient.game.sources.Player
import com.example.polyscrabbleclient.lobby.sources.GameMode
import com.example.polyscrabbleclient.user.User

const val defaultTurnTime = 60
const val millisecondsInSecond = 1000

class GameModel {

    val board: BoardModel = BoardModel()

    var remainingLettersCount = mutableStateOf(0)
    var turnRemainingTime = mutableStateOf(defaultTurnTime)
    var turnTotalTime = mutableStateOf(defaultTurnTime)
    var players: MutableList<Player> = mutableStateListOf<Player>()
    var winnerIndexes: MutableState<List<Int>> = mutableStateOf(listOf())
    var activePlayerIndex = mutableStateOf(0)
    val userLetters = mutableStateListOf<TileModel>()
    var isGameActive = mutableStateOf(false)
    var hasGameJustEnded = mutableStateOf(false)
    val disconnected = mutableStateOf(false)
    var gameMode: MutableState<GameMode> = mutableStateOf(GameMode.Classic)
    var drawnMagicCards: MutableState<List<List<IMagicCard>>> =
        mutableStateOf(listOf(listOf(), listOf(), listOf(), listOf()))

    fun getUser(): Player? {
        return players.find { it.name == User.name }
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
        if (winnerIndex.isNotEmpty()) {
            hasGameJustEnded.value = true
            winnerIndexes.value = winnerIndex
        }
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
        players.clear()
        players.addAll(updatedPlayers.map { player -> Player.fromLightPlayer(player) })
    }

    private fun updateDrawnMagicCards(drawnMagicCard: ArrayList<ArrayList<IMagicCard>>) {
        drawnMagicCards.value = drawnMagicCard
    }

    private fun updateUser() {
        val updatedUser = players.find { player -> player.name == User.name }
        if (updatedUser === null) {
            return
        }
        userLetters.clear()
        userLetters.addAll(updatedUser.letters)
    }

    fun getPlayer(position: Int): Player? {
        return try {
            players[position]
        } catch (e: Exception) {
            null
        }
    }

    fun getUserIndex(): Int {
        val user = getUser() ?: return -1
        return players.indexOf(user)
    }

    fun getActivePlayer(): Player? {
        return getPlayer(activePlayerIndex.value)
    }

    fun isActivePlayer(): Boolean {
        return getActivePlayer() === getUser()
    }

    fun hasGameEnded(): Boolean {
        return !isGameActive.value
    }

    fun updatePlayerName(previousName: String, newName: String) {
        val updatedPlayers = players.map { player ->
            if (player.name == previousName) {
                Player(newName, player.points, player.letters)
            } else {
                player
            }
        }
        players.clear()
        players.addAll(updatedPlayers)
    }

    fun isUserWinner(): Boolean {
        val user = getUser() ?: return false
        val userIndex = players.indexOf(user)
        return winnerIndexes.value.contains(userIndex)
    }

    fun getWinnerNames(): ArrayList<String> {
        return ArrayList(winnerIndexes.value.map { playerIndex -> players[playerIndex].name })
    }
}
