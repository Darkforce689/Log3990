package com.example.polyscrabbleclient.game.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.polyscrabbleclient.game.domain.TileCreator
import com.example.polyscrabbleclient.game.view.draganddrop.DragState
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel

@Composable
fun GameScreen(navController: NavController?, gameViewModel: GameViewModel?) {
    // TODO : USE CompositionLocalProvider ?
    // (https://developer.android.com/jetpack/compose/compositionlocal)
    val dragState = DragState()

    EvenlySpacedRowContainer {
        EvenlySpacedSubColumn {
            Box {
                // TODO : PLAYER INFOS
                Text("PLAYERS INFOS")
            }
            Box {
                // TODO : GAME INFOS
                Text("GAME INFOS")
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
    GameScreen(null, null)
}
