package com.example.polyscrabbleclient.game.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.lobby.domain.ModalActions
import com.example.polyscrabbleclient.lobby.view.ModalView
import com.example.polyscrabbleclient.ui.theme.confirmQuitGameFR
import com.example.polyscrabbleclient.ui.theme.quitButtonFR
import com.example.polyscrabbleclient.ui.theme.warningQuitGameFR

@Composable
fun QuitGameView(
    viewModel: GameViewModel,
    navController: NavController,
    modalButtons: @Composable (
        modalActions: ModalActions
    ) -> Unit
) {
    Text(text = warningQuitGameFR)
    Column {
        modalButtons(
            ModalActions(
                primary = ActionButton(
                    label = { quitButtonFR },
                    action = { viewModel.navigateToMainPage(navController) }
                )
            )
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun QuitGameViewPreview() {
    val gmv: GameViewModel = viewModel()
    ModalView(closeModal = {}, title = confirmQuitGameFR) { modalButtons ->
        QuitGameView(gmv, rememberNavController()) { modalActions ->
            modalButtons(modalActions)
        }
    }
}

