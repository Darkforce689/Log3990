package com.example.polyscrabbleclient.game.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel
import com.example.polyscrabbleclient.ui.theme.cancelButtonFR
import com.example.polyscrabbleclient.ui.theme.exchangeButtonFR
import com.example.polyscrabbleclient.ui.theme.passButtonFR
import com.example.polyscrabbleclient.ui.theme.placeButtonFR

@Composable
fun GameActionsView(viewModel: GameViewModel = GameViewModel()) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            GameActionButton(passButtonFR, { viewModel.canPassTurn() }) {
                viewModel.passTurn()
            }
            GameActionButton(placeButtonFR, { viewModel.canPlaceLetter() }) {
                viewModel.placeLetter()
            }
        }
        Row {
            GameActionButton(exchangeButtonFR, { viewModel.canExchangeLetter() }) {
                viewModel.exchangeLetter()
            }
            GameActionButton(cancelButtonFR, { viewModel.canCancel() }) {
                viewModel.cancel()
            }
        }

    }
}

@Composable
fun GameActionButton(
    label: String,
    enabled: () -> Boolean,
    click: () -> Unit
) {
    Button(
        modifier = Modifier
            .size(
                width = 135.dp,
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
    GameActionsView(GameViewModel())
}
