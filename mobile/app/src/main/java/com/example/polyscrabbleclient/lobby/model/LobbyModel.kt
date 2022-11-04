package com.example.polyscrabbleclient.lobby.model

import com.example.polyscrabbleclient.lobby.sources.ObservableGames
import com.example.polyscrabbleclient.lobby.sources.PendingGameId
import com.example.polyscrabbleclient.lobby.sources.PendingGames
import androidx.compose.runtime.mutableStateOf

class LobbyModel {
    val pendingGames = mutableStateOf<PendingGames?>(null)
    val observableGames = mutableStateOf<ObservableGames?>(null)
    val currentPendingGameId = mutableStateOf<PendingGameId?>(null)
    val pendingGamePlayerNames = mutableStateOf(listOf<String>())
    val isPendingGameHost = mutableStateOf(false)
    val hostHasJustQuitTheGame = mutableStateOf(false)
}
