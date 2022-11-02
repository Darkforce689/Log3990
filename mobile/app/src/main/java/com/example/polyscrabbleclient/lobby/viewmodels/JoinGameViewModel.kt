package com.example.polyscrabbleclient.lobby.viewmodels

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.lobby.sources.JoinGame
import com.example.polyscrabbleclient.lobby.sources.LobbyRepository

class JoinGameViewModel : ViewModel() {
    private val lobby = LobbyRepository
    val pendingGames = lobby.pendingGames

    fun joinGame(
        pendingGameIndex: Int,
        navigateToGameScreen: () -> Unit
    ) {
        val pendingGameId = pendingGames.value?.get(pendingGameIndex)?.id
        if (pendingGameId === null) {
            return
        }
        lobby.emitJoinGame(
            JoinGame(id = pendingGameId),
            navigateToGameScreen
        )
    }
}
