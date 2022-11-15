package com.example.polyscrabbleclient.lobby.viewmodels

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.lobby.sources.JoinGame
import com.example.polyscrabbleclient.lobby.sources.LobbyGameId
import com.example.polyscrabbleclient.lobby.sources.LobbyRepository

class JoinGameViewModel : ViewModel() {
    val lobbyGames = LobbyRepository.model.lobbyGames

    fun joinGame(
        lobbyGameId: LobbyGameId,
        navigateToGameScreen: () -> Unit
    ) {
        LobbyRepository.emitJoinGame(
            JoinGame(lobbyGameId),
            navigateToGameScreen
        )
    }
}
