package com.example.polyscrabbleclient.lobby.view

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.lobby.domain.ModalActions
import com.example.polyscrabbleclient.lobby.view.createGame.GamePasswordInput
import com.example.polyscrabbleclient.lobby.viewmodels.NewGameViewModel
import com.example.polyscrabbleclient.ui.theme.joinGameButtonFR

@Composable
fun EnterPasswordView(
    viewModel: NewGameViewModel,
    modalButtons: @Composable (modalActions: ModalActions) -> Unit
) {
    Column {
        GamePasswordInput(
            password = viewModel.password.value,
            onPasswordChanged = { password ->
                viewModel.password.value = password
            },
            enabled = true
        )

        modalButtons(
            ModalActions(
                primary = ActionButton(
                    label = { joinGameButtonFR },
                    canAction = { viewModel.password.value.isNotEmpty() },
                    action = { viewModel.joinGame(lobbyGames.value) }
                )
            )
        )
    }
}
