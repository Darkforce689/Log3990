package com.example.polyscrabbleclient.game.view

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.polyscrabbleclient.game.view.draganddrop.DragState
import com.example.polyscrabbleclient.game.view.draganddrop.DroppableView
import com.example.polyscrabbleclient.game.viewmodels.BoardViewModel

@Composable
fun BoardView(
    dragState: DragState,
) {
    val viewModel: BoardViewModel = viewModel()
    dragState.onDropCallbacks.add { viewModel.drop(dragState.draggableContent) }

    DroppableView(dragState = dragState)
    {
        BoardCanvasView(dragState, viewModel)
    }
}
