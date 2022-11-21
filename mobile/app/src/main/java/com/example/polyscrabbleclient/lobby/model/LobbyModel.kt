package com.example.polyscrabbleclient.lobby.model

import androidx.compose.runtime.mutableStateOf
import com.example.polyscrabbleclient.lobby.sources.GameMode
import com.example.polyscrabbleclient.lobby.sources.LobbyGameId
import com.example.polyscrabbleclient.lobby.sources.LobbyGamesList

class LobbyModel {
    val selectedGameMode = mutableStateOf(GameMode.Classic)
    val pendingGames = mutableStateOf<LobbyGamesList?>(null)
    val observableGames = mutableStateOf<LobbyGamesList?>(null)
    val currentPendingGameId = mutableStateOf<LobbyGameId?>(null)
    val pendingGamePlayerNames = mutableStateOf(listOf<String>())
    val isPendingGameHost = mutableStateOf(false)
    val hostHasJustQuitTheGame = mutableStateOf(false)
}
