package com.example.polyscrabbleclient.game.view.draganddrop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned

// ADAPTED FROM https://github.com/microsoft/surface-duo-compose-sdk/blob/main/DragAndDrop/library/src/main/java/com/microsoft/device/dualscreen/draganddrop/DropContainer.kt
@Composable
fun DroppableView(
    modifier: Modifier = Modifier,
    dragState: DragState,
    canViewReceiveDrop: () -> Boolean,
    content: @Composable() (BoxScope.() -> Unit),
) {
    var isInBounds by remember { mutableStateOf(false) }
    var dragGlobalPosition by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = modifier.onGloballyPositioned {
        it.boundsInWindow().let { rect ->
            val canvasOrigin = rect.topLeft
            dragGlobalPosition = dragState.startingPosition + dragState.offsetFromStartingPosition
            dragState.currentLocalPosition = dragGlobalPosition - canvasOrigin
            isInBounds = rect.contains(dragGlobalPosition)
        }
    }
    ) {
        // TODO : REMOVE
        println("in?:$isInBounds pos:${dragState.startingPosition} off:${dragState.offsetFromStartingPosition}")
        val canBeDropped =
            dragState.draggableContent !== null &&
            !dragState.isDragging &&
            isInBounds &&
            canViewReceiveDrop()
        if (canBeDropped) {
            dragState.onDrop()
        }
        content()
    }
}
