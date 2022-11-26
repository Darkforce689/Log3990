package com.example.polyscrabbleclient.lobby.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.polyscrabbleclient.lobby.viewmodels.WaitingRoomViewModel
import com.example.polyscrabbleclient.ui.theme.HostRemovedPlayer

@Composable
fun WaitingRoomView(
    navController: NavController,
    viewModel: WaitingRoomViewModel = viewModel()
) {
    val playerRemovedFromGameDialogOpened = remember {
        viewModel.playerRemovedFromGameDialogOpened
    }

    if (viewModel.isPlayerInLobby()) {
        WaitingForOtherPlayersView(navController)
    } else {
        WaitingForHostView(navController, viewModel)
    }

    RemovedFromGameModal(
        navController,
        playerRemovedFromGameDialogOpened,
        viewModel
    )

}

@Composable
private fun RemovedFromGameModal(
    navController: NavController,
    playerRemovedFromGameDialogOpened: MutableState<Boolean>,
    viewModel: WaitingRoomViewModel
) {
    if (playerRemovedFromGameDialogOpened.value) {
        ModalView(
            closeModal = { playerRemovedFromGameDialogOpened.value = false },
            title = HostRemovedPlayer
        ) { modalButtons ->
            RemovedFromGameView(navController ,viewModel) { modalActions ->
                modalButtons(modalActions)
            }
        }
    }
}
