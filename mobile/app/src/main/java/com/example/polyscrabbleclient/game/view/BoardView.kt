package com.example.polyscrabbleclient.game.view

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.polyscrabbleclient.game.model.*
import com.example.polyscrabbleclient.game.view.draganddrop.DragState
import com.example.polyscrabbleclient.game.view.draganddrop.DroppableView

const val ThickDividerWidth = Stroke.DefaultMiter
const val GridDimension = BoardDimension + 1
val BoardSize = 550.dp
val BoardPadding = 10.dp
val GridSize = BoardSize - BoardPadding.times(2)
val GridDivisionSize = GridSize / GridDimension
val HeaderRange = (BoardRange.first+1)..(BoardRange.last+1)
val HeaderTextSize = BoardSize.div(GridDimension).div(1.8f)
val DivisionCenterOffset = GridDivisionSize.times(0.3f);

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
