package com.example.polyscrabbleclient.account.viewmodel

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.account.model.GameStateHistory
import com.example.polyscrabbleclient.account.model.ReplayModel
import com.example.polyscrabbleclient.game.model.BoardModel
import com.example.polyscrabbleclient.game.sources.IMagicCard
import com.example.polyscrabbleclient.game.sources.Player
import com.example.polyscrabbleclient.lobby.sources.GameMode
import kotlin.math.ceil

class ReplayViewModel(private val gameStates: List<GameStateHistory>, gameMode: GameMode) :
    ViewModel() {
    private val gameStateModel = ReplayModel(gameMode)

    init {
        setState(0)
    }

    fun setPage(index: Int) {
        setState(index)
    }

    fun canGetNextPage(index: Int): Boolean {
        return index + 1 <= gameStates.size - 1
    }

    fun canGetPreviousPage(index: Int): Boolean {
        return index - 1 >= 0
    }

    private fun setState(index: Int) {
        gameStateModel.update(gameStates[index].gameState)
    }

    fun getLeftPlayers(): List<Player> {
        return gameStateModel.players.subList(
            0,
            ceil((gameStateModel.players.size / 2).toDouble()).toInt()
        )
    }

    fun getBoard(): BoardModel {
        return gameStateModel.board
    }

    fun getRightPlayers(): List<Player> {
        return gameStateModel.players.subList(
            ceil((gameStateModel.players.size / 2).toDouble()).toInt(),
            gameStateModel.players.size
        )
    }

    fun isActivePlayer(player: Player): Boolean {
        return gameStateModel.isActivePlayer(player)
    }

    fun isMagicGame(): Boolean {
        return gameStateModel.gameMode === GameMode.Magic
    }

    fun getLettersRemaining(): Int {
        return gameStateModel.remainingLettersCount.value
    }

    fun getMagicCards(player: Player): List<IMagicCard> {
        val index = getPlayerIndex(player)
        var cards = gameStateModel.drawnMagicCards.value[index]
        while (cards.size < 3) {
            cards = cards.plus(IMagicCard(""))
        }
        return cards
    }

    fun isCardActive(cardId: String): Boolean {
        return cardId !== ""
    }

    private fun getPlayerIndex(player: Player): Int {
        return gameStateModel.players.indexOf(player)
    }
}
