package com.example.polyscrabbleclient.invitations.viewmodels

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.polyscrabbleclient.NavPage
import com.example.polyscrabbleclient.invitations.models.GameInvite
import com.example.polyscrabbleclient.invitations.models.InvitationAnswer
import com.example.polyscrabbleclient.invitations.sources.ReceivedInvitationRepository
import com.example.polyscrabbleclient.invitations.utils.GameInviteBroker
import com.example.polyscrabbleclient.lobby.sources.JoinGame
import com.example.polyscrabbleclient.lobby.sources.LobbyRepository

class NewInvitationViewModel(private val navController: NavController): ViewModel() {
    var invitation = ReceivedInvitationRepository.invitation

    fun sendInviteAnswer(answer: InvitationAnswer) {
        if (answer == InvitationAnswer.Refuse || invitation.value == null) {
            displayNextPendingInvite()
            return;
        }

        acceptInvite(invitation.value!!)
        clearPendingInvites()
    }

    private fun acceptInvite(invitation: GameInvite) {
        navController.navigate(NavPage.NewGame.label) {
            launchSingleTop = true
        }

        GameInviteBroker.distribute(invitation)

        val (id, password) = invitation.args
        val joinGame = JoinGame(id, password)
        LobbyRepository.quitPendingGame() // TO PREVENT RECEIVING HOST QUITED & KICK FROM HOST
        LobbyRepository.emitJoinGame(joinGame) {
            navController.navigate(NavPage.GamePage.label) {
                launchSingleTop = true
            }
        }
    }

    private fun displayNextPendingInvite() {
        ReceivedInvitationRepository.displayNextPendingInvite()
    }

    private fun clearPendingInvites() {
        ReceivedInvitationRepository.clear()
    }
}
