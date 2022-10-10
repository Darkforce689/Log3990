package com.example.polyscrabbleclient.game.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.polyscrabbleclient.game.view.draganddrop.DragShadow
import com.example.polyscrabbleclient.game.view.draganddrop.DragState
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel

@Composable
fun GameScreen(navController: NavController?, gameViewModel: GameViewModel?) {
    val dragState = DragState()

    var isDroppingItem by remember { mutableStateOf(false) }
    var isItemInBounds by remember { mutableStateOf(false) }

    Column {
        Box {
            DragShadow(
                dragState = dragState
            )
        }
        Box {
            BoardView(
                dragState = dragState,
                onDrag = { inBounds, isDragging ->
                    isDroppingItem = isDragging
                    isItemInBounds = inBounds
                },
            )
        }
        Box {
            LetterRackView(dragState)
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun GameScreenPreview() {
    GameScreen(null, null)
}
