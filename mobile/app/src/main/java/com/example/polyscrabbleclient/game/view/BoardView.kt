package com.example.polyscrabbleclient.game.view

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.polyscrabbleclient.game.view.draganddrop.DragState
import com.example.polyscrabbleclient.game.view.draganddrop.DroppableView
import com.example.polyscrabbleclient.game.viewmodels.BoardViewModel

@Composable
fun BoardView(
    dragState: DragState,
) {
    val viewModel: BoardViewModel = viewModel()
    dragState.onDropActions.receiverAction = { viewModel.drop(it) }
    dragState.onRaiseActions.boardAction = { viewModel.raise(it) }

    DroppableView(
        dragState = dragState,
        canViewReceiveDrop = { viewModel.canPlaceTile() }
    )
    {
        BoardCanvasView(dragState, viewModel)
    }
}
