package com.example.polyscrabbleclient.game.view

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.lobby.domain.ModalActions
import com.example.polyscrabbleclient.ui.theme.backToMainPageButtonFR

@Composable
fun GameDisconnectedView(
    viewModel: GameViewModel,
    navController: NavController,
    modalButtons: @Composable (
        modalActions: ModalActions
    ) -> Unit
) {
    Column {
        modalButtons(
            ModalActions(
                cancel = ActionButton(
                    label = { backToMainPageButtonFR },
                    action = { viewModel.navigateToMainPage(navController) }
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameDisconnectedViewPreview() {
    val gmv: GameViewModel = viewModel()
    GameDisconnectedView(gmv, rememberNavController()) {}
}

