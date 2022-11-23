package com.example.polyscrabbleclient.lobby.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.lobby.sources.BotDifficulty
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
    val pendingGames = LobbyRepository.model.pendingGames
    val observableGames = LobbyRepository.model.observableGames

    val hostHasJustQuitTheGame = LobbyRepository.model.hostHasJustQuitTheGame

    var gameMode = LobbyRepository.model.selectedGameMode
    val timePerTurn = mutableStateOf(DEFAULT_TIMER)
    val numberOfPlayer = mutableStateOf(DEFAULT_PLAYER_NUMBER)
    val randomBonus = mutableStateOf(false)
    val botDifficulty = mutableStateOf(BotDifficulty.Easy)
    var magicCardIds = mutableStateListOf<String>()
    val allMagicCardsSelected = mutableStateOf(false)

    fun containsMagicCard(magicCardId: String): Boolean {
        return magicCardIds.contains(magicCardId)
    }

    fun removeAllSelected() {
        magicCardIds.clear()
    }

    fun addAllSelected() {
        magicCardIds.addAll(magic_card_map.keys)
    }

    fun canCreateGame(): Boolean {
        return (gameMode.value != GameMode.Magic || magicCardIds.isNotEmpty())
    }

    fun sendCreateGameRequest() {
        val newGameParam = CreateGame(
            gameMode = gameMode.value,
            timePerTurn = timePerTurn.value,
            playerNames = ArrayList(),
            randomBonus = randomBonus.value,
            botDifficulty = botDifficulty.value,
            numberOfPlayers = numberOfPlayer.value,
            magicCardIds = ArrayList(magicCardIds),
        )
        LobbyRepository.emitCreateGame(newGameParam)
    }

    fun resetForm() {
        gameMode = LobbyRepository.model.selectedGameMode
        timePerTurn.value = DEFAULT_TIMER
        numberOfPlayer.value = DEFAULT_PLAYER_NUMBER
        randomBonus.value = false
        botDifficulty.value = BotDifficulty.Easy
        magicCardIds = mutableStateListOf()
        allMagicCardsSelected.value = false
    }
}

