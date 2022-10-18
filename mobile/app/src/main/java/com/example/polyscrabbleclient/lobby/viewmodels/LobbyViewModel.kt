package com.example.polyscrabbleclient.lobby.viewmodels

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.lobby.sources.LobbyRepository

class LobbyViewModel: ViewModel() {
    fun joinGame(
        pendingGameIndex: Int,
        navigateToGameScreen: () -> Unit
    ) {
        val pendingGameToken = pendingGames.value?.get(pendingGameIndex)?.id
        if (pendingGameToken === null) {
            return
        }
        lobby.emitJoinGame(pendingGameToken, navigateToGameScreen)
    }

    private val lobby = LobbyRepository
    val pendingGames = lobby.pendingGames
}
