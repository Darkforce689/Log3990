package com.example.polyscrabbleclient.lobby.viewmodels

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.invitations.models.GameInviteArgs
import com.example.polyscrabbleclient.invitations.utils.GameInviteBroker
import com.example.polyscrabbleclient.lobby.sources.LobbyRepository

class WaitingForOtherPlayersViewModel : ViewModel() {
    private val lobby = LobbyRepository
    val hostHasJustQuitTheGame = LobbyRepository.model.hostHasJustQuitTheGame

    fun launchGame(navigateToGameScreen: () -> Unit) {
        lobby.emitLaunchGame(navigateToGameScreen)
        GameInviteBroker.destroyInvite() // TODO Change if join server sends join confirmation
    }

    fun getGameInviteArgs(): GameInviteArgs {
        val id = LobbyRepository.model.currentPendingGameId.value
        if (id == null) {
            return GameInviteArgs(id = "", password = "")
        }
        val password = LobbyRepository.model.password.value
        return GameInviteArgs(id = id, password = password)
    }

    fun canInvite(): Boolean {
        return canLaunchGame()
    }

    fun canLaunchGame(): Boolean {
        return lobby.model.isPendingGameHost.value
            && lobby.model.currentPendingGameId.value !== null
    }

    fun leavePendingGame() {
        lobby.quitPendingGame()
        GameInviteBroker.destroyInvite() // TODO Change if join server sends join confirmation
    }

    fun getPendingGamePlayerNames(): List<String> {
        return lobby.model.pendingGamePlayerNames.value
    }

    fun isHost(playerName: String): Boolean {
        return playerName === getPendingGamePlayerNames()[0]
    }
}
