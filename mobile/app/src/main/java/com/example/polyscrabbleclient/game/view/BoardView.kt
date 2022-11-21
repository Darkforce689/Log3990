package com.example.polyscrabbleclient.game.view

import androidx.compose.runtime.Composable
import com.example.polyscrabbleclient.game.model.BoardModel
import com.example.polyscrabbleclient.game.view.draganddrop.DragState
import com.example.polyscrabbleclient.game.view.draganddrop.DroppableView
import com.example.polyscrabbleclient.game.viewmodels.BoardViewModel

@Composable
fun BoardView(
    dragState: DragState,
    boardModel: BoardModel
) {
    val viewModel: BoardViewModel = BoardViewModel(boardModel)
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
