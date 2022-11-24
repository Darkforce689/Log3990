package com.example.polyscrabbleclient.invitations.utils

import androidx.compose.material.SnackbarDuration
import com.example.polyscrabbleclient.invitations.models.GameInvite
import com.example.polyscrabbleclient.lobby.model.LobbyError
import com.example.polyscrabbleclient.ui.theme.invite_expired
import com.example.polyscrabbleclient.ui.theme.invite_game_full
import com.example.polyscrabbleclient.ui.theme.invite_invalid
import com.example.polyscrabbleclient.utils.SnackBarConfig
import com.example.polyscrabbleclient.utils.SnackBarInvoker

object GameInviteBroker {
    private var invitation: GameInvite? = null

    fun distribute(invitation: GameInvite) {
        this.invitation = invitation
        this.notifyNewInvite()
    }

    fun getCurrentInvitation(): GameInvite? {
        return invitation
    }

    fun destroyInvite() {
        this.invitation = null
    }

    fun reportError(error: LobbyError) {
        val errorMessage = getErrorMessage(error)
        SnackBarInvoker.invoke(SnackBarConfig(errorMessage, "OK", SnackbarDuration.Short))
    }

    private fun getErrorMessage(error: LobbyError): String {
        return when (error) {
            LobbyError.InexistantGame -> invite_expired
            LobbyError.InvalidPassword -> invite_invalid
            LobbyError.NotEnoughPlace -> invite_game_full
        }
    }

    private var inviteCallbacks: MutableList<() -> Unit> = ArrayList()
    fun onInvite(callback: () -> Unit) {
        inviteCallbacks.add(callback)
    }

    private fun notifyNewInvite() {
        inviteCallbacks.forEach {
            it()
        }
    }
}
