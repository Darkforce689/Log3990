package com.example.polyscrabbleclient.game.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.polyscrabbleclient.game.view.draganddrop.DragShadow
import com.example.polyscrabbleclient.game.view.draganddrop.DragState
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel
import com.example.polyscrabbleclient.lobby.view.ModalView
import com.example.polyscrabbleclient.ui.theme.chooseJokerFR
import com.example.polyscrabbleclient.ui.theme.disconnectedFromServerFR

@Composable
fun GameScreen(navController: NavController) {
    val viewModel: GameViewModel = viewModel()

    // TODO : USE CompositionLocalProvider ?
    // (https://developer.android.com/jetpack/compose/compositionlocal)
    val dragState = DragState

    Box(
        modifier = Modifier.zIndex(1f)
    ) {
        DragShadow(dragState)
    }

    EvenlySpacedRowContainer(modifier = Modifier.fillMaxSize()) {
        EvenlySpacedSubColumn(modifier = Modifier.fillMaxHeight()) {
            Box {
                PlayersInfoView(viewModel)
            }
            Box {
                GameInfoView(viewModel)
            }
            Box {
                GameActionsView(viewModel, navController)
            }
        }
        EvenlySpacedSubColumn(modifier = Modifier.fillMaxHeight()) {
            Box {
                BoardView(dragState)
            }
            if (viewModel.isMagicGame()) {
                Box {
                    MagicCardsView(viewModel)
                }
            }
            Box {
                LetterRackView(dragState)
            }
        }
        EvenlySpacedSubColumn(modifier = Modifier.fillMaxHeight()) {
            Box {
                // TODO : RIGHT PANEL
                Text("RIGHT PANEL")
            }
        }
    }

    JokerSelectionModal(viewModel)
    EndOfGameModal(viewModel)
    GameDisconnectedModal(viewModel, navController)
}

@Composable
private fun JokerSelectionModal(viewModel: GameViewModel) {
    val jokerSelectionDialogOpened = viewModel.hasToChooseForJoker()
    if (jokerSelectionDialogOpened.value) {
        ModalView(
            closeModal = { jokerSelectionDialogOpened.value = false },
            title = chooseJokerFR,
        ) { modalButtons ->
            JokerSelectionView(viewModel) { modalActions ->
                modalButtons(modalActions)
            }
        }
    }
}

@Composable
private fun EndOfGameModal(viewModel: GameViewModel) {
    val endOfGameDialogOpened = viewModel.hasGameJustEnded()
    if (endOfGameDialogOpened.value) {
        ModalView(
            closeModal = { endOfGameDialogOpened.value = false },
            title = viewModel.getEndOfGameLabel()
        ) { modalButtons ->
            EndOfGameView(viewModel) { modalActions ->
                modalButtons(modalActions)
            }
        }
    }
}

@Composable
private fun GameDisconnectedModal(viewModel: GameViewModel, navController: NavController) {
    val gameDisconnectedOpened = viewModel.hasGameJustDisconnected()
    if (gameDisconnectedOpened.value) {
        ModalView(
            closeModal = { gameDisconnectedOpened.value = false },
            title = disconnectedFromServerFR
        ) { modalButtons ->
            GameDisconnectedView(viewModel, navController) { modalActions ->
                modalButtons(modalActions)
            }
        }
    }
}

@Composable
fun EvenlySpacedSubColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        content()
    }
}

@Composable
fun EvenlySpacedRowContainer(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        content()
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun GameScreenPreview() {
    GameScreen(navController = rememberNavController())
}
