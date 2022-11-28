package com.example.polyscrabbleclient.lobby.viewmodels

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.lobby.model.CreateGameModel
import com.example.polyscrabbleclient.lobby.sources.CreateGame
import com.example.polyscrabbleclient.lobby.sources.GameMode
import com.example.polyscrabbleclient.lobby.sources.LobbyRepository
import com.example.polyscrabbleclient.utils.constants.magic_card_map

const val DEFAULT_TIMER = 60000
const val MIN_TIMER = 30000
const val MAX_TIMER = 300000
const val MIN_PLAYER_NUMBER = 2
const val MAX_PLAYER_NUMBER = 4
const val DEFAULT_PLAYER_NUMBER = MAX_PLAYER_NUMBER

enum class CreateGameMenu {
    Settings,
    Parameters,
    MagicCards
}

class CreateGameViewModel : ViewModel() {
    var model = CreateGameModel()

    val pendingGames = LobbyRepository.model.pendingGames
    val observableGames = LobbyRepository.model.observableGames

    val hostHasJustQuitTheGame = LobbyRepository.model.hostHasJustQuitTheGame

    fun containsMagicCard(magicCardId: String): Boolean {
        return model.magicCardIds.contains(magicCardId)
    }

    fun removeAllSelected() {
        model.magicCardIds.clear()
    }

    fun addAllSelected() {
        model.magicCardIds.addAll(magic_card_map.keys)
    }

    fun canCreateGame(): Boolean {
        return (model.gameMode.value != GameMode.Magic || model.magicCardIds.isNotEmpty())
    }

    fun sendCreateGameRequest() {
        val newGameParam = CreateGame(
            gameMode = model.gameMode.value,
            timePerTurn = model.timePerTurn.value,
            playerNames = ArrayList(),
            tmpPlayerNames = ArrayList(),
            privateGame = model.isGamePrivate.value,
            password = if (model.isGameProtected.value) model.password.value else null,
            randomBonus = model.randomBonus.value,
            botDifficulty = model.botDifficulty.value,
            numberOfPlayers = model.numberOfPlayer.value,
            magicCardIds = ArrayList(model.magicCardIds),
        )
        LobbyRepository.emitCreateGame(newGameParam)
    }

    fun resetForm() {
        model = CreateGameModel()
    }
}

