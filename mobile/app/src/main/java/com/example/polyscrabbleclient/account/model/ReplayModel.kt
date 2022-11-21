package com.example.polyscrabbleclient.account.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.polyscrabbleclient.game.model.BoardModel
import com.example.polyscrabbleclient.game.sources.GameState
import com.example.polyscrabbleclient.game.sources.IMagicCard
import com.example.polyscrabbleclient.game.sources.LightPlayer
import com.example.polyscrabbleclient.game.sources.Player
import com.example.polyscrabbleclient.lobby.sources.GameMode

class ReplayModel(mode: GameMode) {

    val board: BoardModel = BoardModel()

    var remainingLettersCount = mutableStateOf(0)
    var players = mutableStateListOf<Player>()
    var activePlayerIndex = mutableStateOf(0)
    var gameMode = mode
    var drawnMagicCards =
        mutableStateOf<List<List<IMagicCard>>>(listOf(listOf(), listOf(), listOf(), listOf()))

    fun update(gameState: GameState) {
        board.updateGrid(gameState.grid)
        updatePlayers(gameState.players)
        updateActivePlayerIndex(gameState.activePlayerIndex)
        updateRemainingLetters(gameState.lettersRemaining)
        updateDrawnMagicCards(gameState.drawnMagicCards)
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

    private fun getPlayer(position: Int): Player? {
        return try {
            players[position]
        } catch (e: Exception) {
            null
        }
    }

    private fun getActivePlayer(): Player? {
        return getPlayer(activePlayerIndex.value)
    }

    fun isActivePlayer(player: Player): Boolean {
        return getActivePlayer() === player
    }
}
