package com.example.polyscrabbleclient.lobby.model

import androidx.compose.runtime.mutableStateOf
import com.example.polyscrabbleclient.lobby.sources.GameMode
import com.example.polyscrabbleclient.lobby.sources.LobbyGameId
import com.example.polyscrabbleclient.lobby.sources.LobbyGamesList
import com.example.polyscrabbleclient.lobby.sources.OnlineGameSettings
import kotlinx.coroutines.flow.MutableStateFlow

class LobbyModel {
    val hasJustConfirmedJoin = mutableStateOf<Boolean?>(null)
    val selectedGameMode = mutableStateOf(GameMode.Classic)
    val pendingGames = mutableStateOf<LobbyGamesList?>(null)
    val observableGames = mutableStateOf<LobbyGamesList?>(null)
    val currentPendingGameId = mutableStateOf<LobbyGameId?>(null)
    val pendingGamePlayerNames = mutableStateOf(listOf<String>())
    val candidatePlayerNames = mutableStateOf(listOf<String>())
    val isAcceptedPlayer = mutableStateOf(false)
    val isPendingGameHost = mutableStateOf(false)
    val hostHasJustQuitTheGame = mutableStateOf(false)
    val wasRemovedFromGame = mutableStateOf(false)
    val isGamePrivate = mutableStateOf(false)
    val isGameProtected = mutableStateOf(false)
    val password = mutableStateOf<String?>(null)
    val playerNamesInLobby = MutableStateFlow<List<String>>(listOf())
    val selectedLobbyGame = mutableStateOf<OnlineGameSettings?>(null)
}

enum class LobbyError(val value: String) {
    InexistantGame("INEXISTANT_GAME"),
    InvalidPassword("INVALID_PASSWORD"),
    NotEnoughPlace("PENDING_GAME_FULL")
}
