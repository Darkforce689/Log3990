package com.example.polyscrabbleclient.lobby.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.lobby.sources.BotDifficulty
import com.example.polyscrabbleclient.lobby.sources.CreateGame
import com.example.polyscrabbleclient.lobby.sources.GameMode
import com.example.polyscrabbleclient.lobby.sources.LobbyRepository

const val DEFAULT_TIMER = 60000
const val DEFAULT_PLAYER_NUMBER = 2
const val MIN_TIMER = 30000
const val MAX_TIMER = 300000
const val MIN_PLAYER_NUMBER = 2
const val MAX_PLAYER_NUMBER = 4

class CreateGameViewModel : ViewModel() {

    val hostHasJustQuitTheGame = LobbyRepository.model.hostHasJustQuitTheGame

    val timePerTurn = mutableStateOf(DEFAULT_TIMER)
    val numberOfPlayer = mutableStateOf(DEFAULT_PLAYER_NUMBER)
    val randomBonus = mutableStateOf(false)
    val privateGame = mutableStateOf(false)
    val isPassword = mutableStateOf(false)
    val password = mutableStateOf<String>("")
    val botDifficulty = mutableStateOf(BotDifficulty.Easy)
    val gameMode = mutableStateOf(GameMode.Classic)
    val magicCardIds = mutableStateListOf<String>()

    fun containsMagicCard(magicCardId: String): Boolean {
        return magicCardIds.contains(magicCardId)
    }

    fun canCreateGame(): Boolean {
        return (gameMode.value != GameMode.Magic || magicCardIds.isNotEmpty())
    }

    fun sendCreateGameRequest() {
        val newGameParam = CreateGame(
            gameMode = gameMode.value,
            timePerTurn = timePerTurn.value,
            playerNames = ArrayList(),
            tmpPlayerNames = ArrayList(),
            privateGame = privateGame.value,
            password = if (isPassword.value) password.value else null,
            randomBonus = randomBonus.value,
            botDifficulty = botDifficulty.value,
            numberOfPlayers = numberOfPlayer.value,
            magicCardIds = ArrayList(magicCardIds),
        )
        LobbyRepository.emitCreateGame(newGameParam)
    }
}

