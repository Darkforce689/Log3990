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
    content: @Composable (BoxScope.() -> Unit),
) {
    var isInBounds by remember { mutableStateOf(false) }
    var dragGlobalPosition by remember { mutableStateOf(Offset.Zero) }
    val offsetFromStartingPosition = dragState.offsetFromStartingPosition

    Box(modifier = modifier.onGloballyPositioned {
        it.boundsInWindow().let { rect ->
            val canvasOrigin = rect.topLeft
            dragGlobalPosition = dragState.startingPosition + offsetFromStartingPosition
            dragState.currentLocalPosition = dragGlobalPosition - canvasOrigin
            isInBounds = rect.contains(dragGlobalPosition)
        }
    }) {
        val isDropHappening = dragState.draggableContent !== null && !dragState.isDragging
        if (isDropHappening) {
            if (isInBounds) {
                // View can process the drop since it is in bounds
                if (canViewReceiveDrop()) {
                    // View can accept the drop since the new tile
                    // will replace a null tile or a transient tile
                    dragState.onDrop()
                } else {
                    // View cannot accept the drop since the new tile
                    // would replace a permanent tile
                    dragState.cancelDrop()
                }
            } else {
                // View cannot accept the drop since the new tile
                // is outside of bounds
                dragState.cancelDrop()
            }
        }
        content()
    }
}
