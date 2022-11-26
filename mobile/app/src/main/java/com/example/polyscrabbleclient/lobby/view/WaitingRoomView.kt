package com.example.polyscrabbleclient.lobby.view

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.polyscrabbleclient.lobby.viewmodels.WaitingRoomViewModel

@Composable
fun WaitingRoomView(
    navController: NavController,
    viewModel: WaitingRoomViewModel = viewModel()
) {
    if (viewModel.isPlayerInLobby()) {
        WaitingForOtherPlayersView(navController)
    } else {
        WaitingForHostView(navController, viewModel)
    }
}
