package com.example.polyscrabbleclient.lobby.view

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.lobby.domain.ModalActions
import com.example.polyscrabbleclient.lobby.view.createGame.GamePasswordInput
import com.example.polyscrabbleclient.lobby.viewmodels.JoinGameViewModel
import com.example.polyscrabbleclient.ui.theme.joinGameButtonFR

@Composable
fun EnterPasswordView(
    viewModel: JoinGameViewModel,
    navController: NavController,
    modalButtons: @Composable (modalActions: ModalActions) -> Unit
) {
    Column {
        GamePasswordInput(
            password = viewModel.enteredPassword.value,
            onPasswordChanged = { password ->
                viewModel.enteredPassword.value = password
            },
            enabled = true
        )

        modalButtons(
            ModalActions(
                primary = ActionButton(
                    label = { joinGameButtonFR },
                    canAction = { viewModel.enteredPassword.value.isNotEmpty() },
                    action = { viewModel.joinGame(navController) }
                )
            )
        )
    }
}
