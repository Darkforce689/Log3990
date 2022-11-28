package com.example.polyscrabbleclient.lobby.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.invitations.utils.GameInviteBroker
import com.example.polyscrabbleclient.lobby.model.LobbyError
import com.example.polyscrabbleclient.lobby.sources.Error
import com.example.polyscrabbleclient.lobby.sources.LobbyRepository

class NewGameViewModel : ViewModel() {
    val createGameDialogOpened = mutableStateOf(false)
    val joinGameDialogOpened = mutableStateOf(false)
    val watchGameDialogOpened = mutableStateOf(false)
    val enterGamePasswordDialogOpened = mutableStateOf(false)

    private var isInvitation = false

    init {
        listenForLobbyErrors()
        refreshState()
        GameInviteBroker.onInvite {
            onInvitation()
        }
    }

    private fun listenForLobbyErrors() {
        LobbyRepository.subscribeOnError("NewGameViewModelError") {
            if (it == null) {
                return@subscribeOnError
            }
            Thread {
                if (isInvitation) {
                    onInvitationError(it)
                }
                LobbyRepository.leaveLobbyGame()
                isInvitation = false
            }.start()
        }
    }

    private fun onInvitationError(errorContent: Error?) {
        GameInviteBroker.destroyInvite()
        if (errorContent === null) {
            return
        }
        val lobbyError = LobbyError.values().find {
            it.value == errorContent
        } ?: return

        GameInviteBroker.reportError(lobbyError)
    }

    private fun onInvitation() {
        createGameDialogOpened.value = false
        joinGameDialogOpened.value = false
        watchGameDialogOpened.value = false
        enterGamePasswordDialogOpened.value = false
        isInvitation = true
    }

    private fun refreshState() {
        GameInviteBroker.getCurrentInvitation() ?: return
        onInvitation()
    }
}
