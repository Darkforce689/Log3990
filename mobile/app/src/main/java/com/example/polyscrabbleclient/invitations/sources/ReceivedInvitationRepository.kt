package com.example.polyscrabbleclient.invitations.sources

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.polyscrabbleclient.auth.AppSocketHandler
import com.example.polyscrabbleclient.auth.OnAppEvent
import com.example.polyscrabbleclient.game.sources.GameRepository
import com.example.polyscrabbleclient.invitations.models.GameInvite
import com.example.polyscrabbleclient.invitations.models.GameInviteDTO
import com.example.polyscrabbleclient.invitations.utils.InvitationFactory
import kotlin.collections.ArrayDeque

object ReceivedInvitationRepository {
    var invitation: MutableState<GameInvite?> = mutableStateOf(null)

    private val onNewInvite: (invitationDTO: GameInviteDTO?) -> Unit = { invitationDTO ->
        receiveNewInvitation(invitationDTO)
    }

    private fun receiveNewInvitation(invitationDTO: GameInviteDTO?) {
        // If in game discard invitation
        if (GameRepository.model.isGameActive.value) {
            return
        }
        invitationDTO?.let { gameInviteDTO ->
            InvitationFactory.createGameInvite(gameInviteDTO) {
                addInvitation(it)
            }
        }
    }

    private fun listenForInvitations() {
        AppSocketHandler.ensureConnection()
        AppSocketHandler.on(OnAppEvent.Invitation, onNewInvite)
    }

    private fun addInvitation(newInvitation: GameInvite) {
        this.pendingInvites.add(newInvitation)
        if (invitation.value == null) {
            this.displayNextPendingInvite()
        }
    }

    init {
        listenForInvitations()
    }

    private val pendingInvites: ArrayDeque<GameInvite> = ArrayDeque()

    private fun getNextInvite(): GameInvite? {
        return if (pendingInvites.isEmpty()) null else pendingInvites.removeFirst()
    }

    fun displayNextPendingInvite() {
        Thread {
            val prevValue = invitation.value
            invitation.value = null
            if (prevValue != null) {
                Thread.sleep(300)
            }
            val nextInvite = getNextInvite()
            invitation.value = nextInvite
        }.start()
    }

    fun clear() {
        invitation.value = null
        pendingInvites.clear()
    }
}
