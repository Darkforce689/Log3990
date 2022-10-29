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
import com.example.polyscrabbleclient.ui.theme.*

@Composable
fun GameActionsView(viewModel: GameViewModel = GameViewModel(), navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FlexedRow (2) { width ->
            GameActionButton(width, passButtonFR, { viewModel.canPassTurn() }) {
                viewModel.passTurn()
            }
            GameActionButton(width, placeButtonFR, { viewModel.canPlaceLetter() }) {
                viewModel.placeLetter()
            }
        }
        FlexedRow ( 2) { width ->
            GameActionButton(width, exchangeButtonFR, { viewModel.canExchangeLetter() }) {
                viewModel.exchangeLetter()
            }
            GameActionButton(width, cancelButtonFR, { viewModel.canCancel() }) {
                viewModel.cancel()
            }
        }
        FlexedRow (1) { width ->
            GameActionButton(width, viewModel.getQuitLabel(), { true }) {
                viewModel.quitGame()
                navController.navigate(NavPage.MainPage.label) {
                    launchSingleTop = true
                }
            }
        }
    }
}

@Composable
fun FlexedRow (
    contentSize: Int,
    width: Dp = 270.dp,
    content: @Composable (width: Dp) -> Unit
) {
    Row {
        content(width = width.div(contentSize))
    }
}

@Composable
fun GameActionButton(
    width: Dp,
    label: String,
    enabled: () -> Boolean,
    click: () -> Unit
) {
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
        onClick = click,
        enabled = enabled()
    ) {
        Text(label)
    }
}

@Preview(showBackground = true)
@Composable
fun GameActionsPreview() {
    GameActionsView(GameViewModel(), rememberNavController())
}
