package com.example.polyscrabbleclient.invitations.utils

import com.example.polyscrabbleclient.invitations.models.GameInvite
import com.example.polyscrabbleclient.invitations.models.GameInviteDTO
import com.example.polyscrabbleclient.user.UserRepository

object InvitationFactory {
    fun createGameInvite(invitationDTO: GameInviteDTO, callback: (invitation: GameInvite) -> Unit): Thread {
        val task = Thread {
            val from = invitationDTO.from
            UserRepository.getUser(from) { user ->
                val username = user.name
                val invitation: GameInvite = invitationDTO.copy(from = username)
                callback(invitation)
            }
        }
        task.start()
        return task
    }
}
