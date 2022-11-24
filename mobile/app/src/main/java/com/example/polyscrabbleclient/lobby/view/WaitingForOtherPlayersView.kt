package com.example.polyscrabbleclient.lobby.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.polyscrabbleclient.NavPage
import com.example.polyscrabbleclient.invitations.components.InviteUserToGameModal
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.lobby.domain.ModalActions
import com.example.polyscrabbleclient.lobby.viewmodels.WaitingForOtherPlayersViewModel
import com.example.polyscrabbleclient.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun WaitingForOtherPlayersView (
    navController: NavController,
    modalButtons: @Composable (
        modalActions: ModalActions
    ) -> Unit
) {
    val viewModel : WaitingForOtherPlayersViewModel = viewModel()

    Column {

        Text(text = pendingGameIdFR(viewModel.getPendingGameId()))

        Column {
            viewModel.getPendingGamePlayerNames().forEach {
                Text(text = "> $it")
            }
        }

        // Invite some one
        if (viewModel.canInvite()) {
            var isInviteModalOpened by remember {
                mutableStateOf(false)
            }
            Button(onClick = { isInviteModalOpened = true }) {
                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null)
            }

            // TODO if waiting modal stop recomposing notify oli
            InviteUserToGameModal(
                inviteArgs = viewModel.getGameInviteArgs(),
                nonInvitableUserNames = viewModel.getPendingGamePlayerNames(),
                isOpened = isInviteModalOpened,
                onClose = { isInviteModalOpened = false }
            )
        }


        // TODO : ACCEPT / KICK PLAYERS

    }

    modalButtons(
        ModalActions(
            cancel = ActionButton(
                action = { viewModel.leavePendingGame() }
            ),
            primary = ActionButton(
                label = { launchGameButtonFR },
                canAction = { viewModel.canLaunchGame() },
                action = { viewModel.launchGame { navigateToGameScreen(navController) } }
            )
        )
    )
}

private fun navigateToGameScreen(navController: NavController) {
    CoroutineScope(Dispatchers.IO).launch {
        launch(Dispatchers.Main) {
            navController.navigate(NavPage.GamePage.label) {
                launchSingleTop = true
            }
        }
    }
}


