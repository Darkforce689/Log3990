package com.example.polyscrabbleclient.invitations.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.polyscrabbleclient.invitations.viewmodels.NewInvitationViewModel

@Composable
fun NewInvitationView(
    navController: NavController,
) {
    val viewModel: NewInvitationViewModel = NewInvitationViewModel(navController)
    NewInvitationModal(
        invitation = viewModel.invitation.value,
        onClose = { answer ->
            viewModel.sendInviteAnswer(answer)
        }
    )
}
