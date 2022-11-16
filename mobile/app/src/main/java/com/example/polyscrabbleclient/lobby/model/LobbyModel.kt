package com.example.polyscrabbleclient.lobby.model

import androidx.compose.runtime.mutableStateOf
import com.example.polyscrabbleclient.lobby.sources.LobbyGameId
import com.example.polyscrabbleclient.lobby.sources.LobbyGames

class LobbyModel {
    val lobbyGames = mutableStateOf<LobbyGames?>(null)
    val currentPendingGameId = mutableStateOf<LobbyGameId?>(null)
    val pendingGamePlayerNames = mutableStateOf(listOf<String>())
    val isPendingGameHost = mutableStateOf(false)
    val hostHasJustQuitTheGame = mutableStateOf(false)
}
