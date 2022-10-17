package com.example.polyscrabbleclient.lobby.viewmodels

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.lobby.sources.JoinGame
import com.example.polyscrabbleclient.lobby.sources.LobbyRepository

class LobbyViewModel: ViewModel() {
    fun joinGame(pendingGameIndex: Int) {
        val pendingGameToken = pendingGames.value?.get(pendingGameIndex)?.id
        if (pendingGameToken === null) {
            return
        }
        lobby.emitJoinGame(pendingGameToken)
    }

    private val lobby = LobbyRepository
    val pendingGames = lobby.pendingGames
}
