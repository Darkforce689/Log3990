package com.example.polyscrabbleclient.game.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.polyscrabbleclient.NavPage
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.ui.theme.*

@Composable
fun GameActionsView(viewModel: GameViewModel = GameViewModel(), navController: NavController) {
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
                action = {
                    viewModel.quitGame()
                    viewModel.navigateToMainPage(navController)
                }
            )
        )

        FlexedSquaredContainer(
            contentCount = 2,
            contents = gameButtonsContents,
            view = GameActionButton
        )
    }
}

val GameActionButton: @Composable (
    width: Dp,
    actionButton: ActionButton
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
