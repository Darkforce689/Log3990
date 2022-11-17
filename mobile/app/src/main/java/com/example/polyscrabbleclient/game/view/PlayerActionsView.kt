package com.example.polyscrabbleclient.game.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.ui.theme.*

@Composable
fun PlayerActionButtons(
    viewModel: GameViewModel,
    quitAction: () -> Unit
) {
    val gameButtonsContents = listOf(
        ActionButton(
            label = { passButtonFR },
            canAction = { viewModel.canPassTurn() },
            action = { viewModel.passTurn() },
            icon = Icons.Filled.SkipNext
        ),
        ActionButton(
            label = { placeButtonFR },
            canAction = { viewModel.canPlaceLetter() },
            action = { viewModel.placeLetter() },
            icon = Icons.Filled.Download
        ),
        ActionButton(
            label = { exchangeButtonFR },
            canAction = { viewModel.canExchangeLetter() },
            action = { viewModel.exchangeLetter() },
            icon = Icons.Filled.SwapVert
        ),
        ActionButton(
            label = { cancelButtonFR },
            canAction = { viewModel.canCancel() },
            action = { viewModel.cancel() },
            icon = Icons.Filled.Block
        ),
        ActionButton(
            label = { viewModel.getQuitLabel() },
            canAction = { true },
            action = { quitAction() },
            icon = Icons.Filled.Logout
        )
    )

    FlexedSquaredContainer(
        contentCount = 2,
        contents = gameButtonsContents,
        view = GameActionButton,
        size = 335.dp
    )
}
