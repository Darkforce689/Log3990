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
        return lobby.isPendingGameHost.value
    }

    fun leavePendingGame() {
        LobbyRepository.quitPendingGame()
    }

    fun getPendingGameId(): String? {
        return LobbyRepository.currentPendingGameId.value
    }

    fun getPendingGamePlayerNames(): List<String> {
        return LobbyRepository.pendingGamePlayerNames.value
    }
}
