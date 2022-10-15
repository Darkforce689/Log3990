package com.example.polyscrabbleclient.game.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.polyscrabbleclient.game.view.draganddrop.DragShadow
import com.example.polyscrabbleclient.game.view.draganddrop.DragState
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun GameScreen(navController: NavController?/*, gameViewModel: GameViewModel?*/) {
    val viewModel: GameViewModel = viewModel()

    // TODO : USE CompositionLocalProvider ?
    // (https://developer.android.com/jetpack/compose/compositionlocal)
    val dragState = DragState()

    Box(
        modifier = Modifier.zIndex(1f)
    ) {
        DragShadow(dragState)
    }

    EvenlySpacedRowContainer {
        EvenlySpacedSubColumn {
            Box {
                PlayersInfoView(viewModel)
            }
            Box {
                GameInfoView(viewModel)
            }
            Box {
                // TODO : GAME ACTIONS
                Text("GAME ACTIONS")
            }
        }
        EvenlySpacedSubColumn {
            Box {
                BoardView(dragState)
            }
            Box {
                LetterRackView(dragState)
            }
        }
        EvenlySpacedSubColumn {
            Box {
                // TODO : RIGHT PANEL
                Text("RIGHT PANEL")
            }
        }
    }

    // TODO : REMOVE
    LaunchedEffect(Unit) {
        while(true) {
            delay(2000)
            viewModel.turnRemainingTime.value = viewModel.turnRemainingTime.value - 2
            viewModel.game.getActivePlayer()?.let {
                p -> p.points.value = p.points.value + 10
            }
            viewModel.game.setNextActivePlayer()
        }
    }
}

@Composable
fun EvenlySpacedSubColumn(content: @Composable ColumnScope.() -> Unit) {
    Column (
        modifier = Modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        content()
    }
}

@Composable
fun EvenlySpacedRowContainer(content: @Composable RowScope.() -> Unit) {
    Row (
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        content()
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun GameScreenPreview() {
    GameScreen(null)
}
