package com.example.polyscrabbleclient.lobby.viewmodels

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.lobby.sources.JoinGame
import com.example.polyscrabbleclient.lobby.sources.LobbyRepository

class JoinGameViewModel : ViewModel() {
    val pendingGames = LobbyRepository.model.pendingGames

    fun joinGame(
        pendingGameIndex: Int,
        navigateToGameScreen: () -> Unit
    ) {
        val pendingGameId = LobbyRepository.model.pendingGames.value?.get(pendingGameIndex)?.id
        if (pendingGameId === null) {
            return
        }
        LobbyRepository.emitJoinGame(
            JoinGame(id = pendingGameId),
            navigateToGameScreen
        )
    }
}
