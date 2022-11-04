package com.example.polyscrabbleclient.lobby.viewmodels

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.game.sources.GameRepository
import com.example.polyscrabbleclient.lobby.sources.LobbyRepository

class WaitingForOtherPlayersViewModel : ViewModel() {
    private val lobby = LobbyRepository

    fun launchGame(navigateToGameScreen: () -> Unit) {
        lobby.emitLaunchGame(navigateToGameScreen)
    }

    fun canLaunchGame(): Boolean {
        return lobby.model.isPendingGameHost.value
    }

    fun leavePendingGame() {
        lobby.quitPendingGame()
    }

    fun getPendingGameId(): String? {
        return lobby.model.currentPendingGameId.value
    }

    fun getPendingGamePlayerNames(): List<String> {
        return lobby.model.pendingGamePlayerNames.value
    }
}
