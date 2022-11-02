package com.example.polyscrabbleclient.lobby.viewmodels

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

    val hostHasJustQuitTheGame = LobbyRepository.hostHasJustQuitTheGame

    val timePerTurn = mutableStateOf(DEFAULT_TIMER)
    val numberOfPlayer = mutableStateOf(DEFAULT_PLAYER_NUMBER)
    val randomBonus = mutableStateOf(false)
    val botDifficulty = mutableStateOf(BotDifficulty.Easy)
    val gameMode = mutableStateOf(GameMode.Classic)

    fun sendCreateGameRequest() {
        val newGameParam = CreateGame(
            gameMode = gameMode.value,
            timePerTurn = timePerTurn.value,
            playerNames = ArrayList(),
            randomBonus = randomBonus.value,
            botDifficulty = botDifficulty.value,
            numberOfPlayers = numberOfPlayer.value,
        )
        LobbyRepository.emitCreateGame(newGameParam)
    }
}

