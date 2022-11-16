package com.example.polyscrabbleclient.game.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoveDown
import androidx.compose.material.icons.filled.MoveUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.ui.theme.FlexedSquaredContainer
import com.example.polyscrabbleclient.ui.theme.NextPlayer
import com.example.polyscrabbleclient.ui.theme.PreviousPlayer
import com.example.polyscrabbleclient.ui.theme.leaveGameButtonFR

@Composable
fun ObserverActionButtons(
    viewModel: GameViewModel,
    navController: NavController
) {
    val observerButtonsContents = listOf(
        ActionButton(
            label = { NextPlayer },
            action = { viewModel.watchNextPlayer() },
            icon = Icons.Filled.MoveDown
        ),
        ActionButton(
            label = { PreviousPlayer },
            action = { viewModel.watchPreviousPlayer() },
            icon = Icons.Filled.MoveUp
        ),
        ActionButton(
            label = { leaveGameButtonFR },
            action = { viewModel.navigateToMainPage(navController) },
            icon = Icons.Filled.Logout
        )
    )

    FlexedSquaredContainer(
        contentCount = 1,
        contents = observerButtonsContents,
        view = GameActionButton,
        size = 335.dp
    )
}
