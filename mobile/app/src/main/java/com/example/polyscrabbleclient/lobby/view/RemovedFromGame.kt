package com.example.polyscrabbleclient.lobby.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.lobby.domain.ModalActions
import com.example.polyscrabbleclient.lobby.viewmodels.WaitingRoomViewModel
import com.example.polyscrabbleclient.ui.theme.Ok
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController

@Composable
fun RemovedFromGameView(
    navController: NavController,
    viewModel: WaitingRoomViewModel = viewModel(),
    modalButtons: @Composable (
        modalActions: ModalActions
    ) -> Unit
) {
    modalButtons(
        ModalActions(
            cancel = ActionButton(
                label = { Ok },
                action = {
                    viewModel.leaveLobbyGame(navController)
                }
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
fun RemovedFromGamePreview() {
    RemovedFromGameView(rememberNavController()) {}
}
