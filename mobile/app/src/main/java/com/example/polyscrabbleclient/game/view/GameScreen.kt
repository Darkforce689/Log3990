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
    // TODO : USE CompositionLocalProvider ?
    // (https://developer.android.com/jetpack/compose/compositionlocal)
    val dragState = DragState()

    Column {
        Box {
            DragShadow(dragState)
        }
        Box {
            BoardView(dragState)
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
