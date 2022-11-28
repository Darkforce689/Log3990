package com.example.polyscrabbleclient.lobby.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.polyscrabbleclient.lobby.sources.BotDifficulty
import com.example.polyscrabbleclient.lobby.sources.LobbyRepository
import com.example.polyscrabbleclient.lobby.viewmodels.DEFAULT_PLAYER_NUMBER
import com.example.polyscrabbleclient.lobby.viewmodels.DEFAULT_TIMER

class CreateGameModel {
    var gameMode = LobbyRepository.model.selectedGameMode
    val timePerTurn = mutableStateOf(DEFAULT_TIMER)
    val numberOfPlayer = mutableStateOf(DEFAULT_PLAYER_NUMBER)
    val randomBonus = mutableStateOf(false)
    val isGamePrivate = mutableStateOf(false)
    val isGameProtected = mutableStateOf(false)
    val password = mutableStateOf("")
    val botDifficulty = mutableStateOf(BotDifficulty.Easy)
    var magicCardIds = mutableStateListOf<String>()
    val allMagicCardsSelected = mutableStateOf(false)
}
