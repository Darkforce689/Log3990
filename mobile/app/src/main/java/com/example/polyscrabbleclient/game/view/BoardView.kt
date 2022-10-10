package com.example.polyscrabbleclient.game.view

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.polyscrabbleclient.game.model.*
import com.example.polyscrabbleclient.game.view.draganddrop.DragState
import com.example.polyscrabbleclient.game.view.draganddrop.DroppableView

@Composable
fun BoardView(
    onDrag: (inBounds: Boolean, isDragging: Boolean) -> Unit,
    dragState: DragState,
) {
    DroppableView(
        onDrag = onDrag,
        dragState = dragState,
    )
    {
        BoardCanvasView(dragState)
    }
}
