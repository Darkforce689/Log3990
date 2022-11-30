package com.example.polyscrabbleclient.account.viewmodel

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.account.model.GameStateHistory
import com.example.polyscrabbleclient.account.model.ReplayModel
import com.example.polyscrabbleclient.game.model.BoardModel
import com.example.polyscrabbleclient.game.sources.IMagicCard
import com.example.polyscrabbleclient.game.sources.Player
import com.example.polyscrabbleclient.lobby.sources.GameMode
import com.example.polyscrabbleclient.user.User
import com.example.polyscrabbleclient.user.UserRepository
import com.example.polyscrabbleclient.utils.constants.NoAvatar
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
        return getOrderedPlayers().subList(
            0,
            ceil((gameStateModel.players.size / 2).toDouble()).toInt()
        )
    }

    fun getBoard(): BoardModel {
        return gameStateModel.board
    }

    fun getRightPlayers(): List<Player> {
        return getOrderedPlayers().subList(
            ceil((gameStateModel.players.size / 2).toDouble()).toInt(),
            gameStateModel.players.size
        )
    }

    private fun getOrderedPlayers(): List<Player> {
        val userIndex = gameStateModel.players.indexOfFirst { player -> User.name == player.name }
        if (userIndex < 0) {
            return gameStateModel.players
        }
        val before = gameStateModel.players.subList(0, userIndex)
        val after = gameStateModel.players.subList(userIndex, gameStateModel.players.size)
        return after + before
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

    fun getAvatar(name: String): String {
        var avatar: String = NoAvatar
        val thread = Thread {
            UserRepository.getUserByName(name) {
                avatar = it.avatar
            }
        }
        thread.start()
        thread.join()
        return avatar
    }

    private fun getPlayerIndex(player: Player): Int {
        return gameStateModel.players.indexOf(player)
    }
}
