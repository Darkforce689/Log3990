package com.example.polyscrabbleclient.lobby.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.lobby.domain.ModalActions
import com.example.polyscrabbleclient.lobby.viewmodels.JoinGameViewModel
import com.example.polyscrabbleclient.ui.theme.Ok

@Composable
fun BadPasswordView(
    navController: NavController,
    viewModel: JoinGameViewModel = viewModel(),
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
fun BadPasswordPreview() {
    BadPasswordView(rememberNavController()) {}
}
