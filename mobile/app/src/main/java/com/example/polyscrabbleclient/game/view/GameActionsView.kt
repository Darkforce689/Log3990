package com.example.polyscrabbleclient.game.view

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel
import com.example.polyscrabbleclient.lobby.view.ModalView
import com.example.polyscrabbleclient.ui.theme.confirmQuitGameFR
import com.example.polyscrabbleclient.utils.PhysicalButtons

@Composable
fun GameActionsView(viewModel: GameViewModel = GameViewModel(), navController: NavController) {

    var playerQuitGameDialogOpened by remember {
        mutableStateOf(false)
    }

    val quitAction: () -> Unit = {
        if (viewModel.hasGameEnded()) {
            viewModel.navigateToMainPage(navController)
        } else {
            playerQuitGameDialogOpened = true
        }
    }

    PhysicalButtons.backPressed = quitAction

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (viewModel.isObserver()) {
            ObserverActionButtons(viewModel, navController)
            // TODO : OVERRIDE PhysicalButtons.backPressed
        } else {
            PlayerActionButtons(viewModel, quitAction)
        }
    }

    if (playerQuitGameDialogOpened) {
        ModalView(
            closeModal = { playerQuitGameDialogOpened = false },
            title = confirmQuitGameFR
        ) { modalButtons ->
            QuitGameView(viewModel, navController) { modalActions ->
                modalButtons(modalActions)
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun GameActionsPreview() {
    GameActionsView(GameViewModel(), rememberNavController())
}
