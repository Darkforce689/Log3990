package com.example.polyscrabbleclient.invitations.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.polyscrabbleclient.invitations.models.GameInvite
import com.example.polyscrabbleclient.invitations.models.GameInviteArgs
import com.example.polyscrabbleclient.invitations.models.InvitationAnswer
import com.example.polyscrabbleclient.invitations.models.InvitationType
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.lobby.domain.ModalActions
import com.example.polyscrabbleclient.lobby.view.ModalView
import com.example.polyscrabbleclient.ui.theme.*

@Composable
fun NewInvitationModal(
    invitation: GameInvite?,
    onClose: (InvitationAnswer) -> Unit,
) {
    if (invitation == null) {
        return
    }

    ModalView(
        closeModal = {},
        title = new_invite_title,
    ) { modalButtons ->
        NewInvitationModalContent(invitation, onClose) { modalActions ->
            modalButtons(modalActions)
        }
    }
}

@Composable
fun NewInvitationModalContent(
    invitation: GameInvite,
    onClose: (InvitationAnswer) -> Unit,
    modalButtons: @Composable (
        modalActions: ModalActions
    ) -> Unit
) {
    Column {
        Text(new_invite_body(invitation.from))
    }
    modalButtons(
        ModalActions(
            cancel = ActionButton(
                label = { refuse_invite_button },
                action = {
                    onClose(InvitationAnswer.Refuse)
                }
            ),
            primary = ActionButton(
                label = { accept_invite_button },
                action = {
                    onClose(InvitationAnswer.Accept)
                }
            )
        ),
    )
}

@Preview(showBackground = true)
@Composable
fun NewInvitationModalPreview() {
    val args = GameInviteArgs(id = "abc", password = null)
    val invitation = GameInvite(from = "olivier", to = "abc", type=InvitationType.Game, args = args, date = "nodate")
    NewInvitationModal(invitation = invitation, onClose = {})
}
