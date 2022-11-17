package com.example.polyscrabbleclient.game.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.lobby.view.ModalView
import com.example.polyscrabbleclient.ui.theme.*
import com.example.polyscrabbleclient.utils.PhysicalButtons

@Composable
fun GameActionsView(viewModel: GameViewModel = GameViewModel(), navController: NavController) {

    var playerQuitGameDialogOpened by remember {
        mutableStateOf(false)
    }

    fun quitAction() {
        if (viewModel.hasGameEnded()) {
            viewModel.navigateToMainPage(navController)
            PhysicalButtons.popBackPress()
        } else {
            playerQuitGameDialogOpened = true
        }
        PhysicalButtons.pushBackPress { quitAction() }
    }

    PhysicalButtons.pushBackPress { quitAction() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val gameButtonsContents = listOf(
            ActionButton(
                label = { passButtonFR },
                canAction = { viewModel.canPassTurn() },
                action = { viewModel.passTurn() }
            ),
            ActionButton(
                label = { placeButtonFR },
                canAction = { viewModel.canPlaceLetter() },
                action = { viewModel.placeLetter() }
            ),
            ActionButton(
                label = { exchangeButtonFR },
                canAction = { viewModel.canExchangeLetter() },
                action = { viewModel.exchangeLetter() }
            ),
            ActionButton(
                label = { cancelButtonFR },
                canAction = { viewModel.canCancel() },
                action = { viewModel.cancel() }
            ),
            ActionButton(
                label = { viewModel.getQuitLabel() },
                canAction = { true },
                action = { quitAction() }
            )
        )

        FlexedSquaredContainer(
            contentCount = 2,
            contents = gameButtonsContents,
            view = GameActionButton
        )

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
}

val GameActionButton: @Composable (
    width: Dp,
    actionButton: ActionButton,
) -> Unit = { width, actionButton ->
    Button(
        modifier = Modifier
            .size(
                width = width,
                height = 90.dp
            )
            .padding(
                vertical = 15.dp,
                horizontal = 15.dp
            ),
        onClick = actionButton.action,
        enabled = actionButton.canAction()
    ) {
        Text(actionButton.label())
    }
}

@Preview(showBackground = true)
@Composable
fun GameActionsPreview() {
    GameActionsView(GameViewModel(), rememberNavController())
}
