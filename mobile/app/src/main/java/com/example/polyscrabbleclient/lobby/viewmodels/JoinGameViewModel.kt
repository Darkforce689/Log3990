package com.example.polyscrabbleclient.lobby.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.lobby.sources.JoinGame
import com.example.polyscrabbleclient.lobby.sources.LobbyGameId
import com.example.polyscrabbleclient.lobby.sources.LobbyGamesList
import com.example.polyscrabbleclient.lobby.sources.LobbyRepository

class JoinGameViewModel : ViewModel() {
    val selectedGameMode = LobbyRepository.model.selectedGameMode

    val selectedGameIndex = mutableStateOf<Int?>(null)

    fun joinGame(
        lobbyGames: LobbyGamesList?,
        navigateToGameScreen: () -> Unit
    ) {
        val lobbyGameId = selectedGameIndex.value?.let { lobbyGames?.get(it) }?.id
        if (lobbyGameId === null) {
            return
        }
        LobbyRepository.emitJoinGame(
            JoinGame(lobbyGameId),
            navigateToGameScreen
        )
    }
}
