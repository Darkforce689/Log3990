package com.example.polyscrabbleclient.game.view.draganddrop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import com.example.polyscrabbleclient.game.view.DragData
import com.example.polyscrabbleclient.game.view.DragState

// FROM https://github.com/microsoft/surface-duo-compose-sdk/blob/main/DragAndDrop/library/src/main/java/com/microsoft/device/dualscreen/draganddrop/DropContainer.kt
@Composable
fun DroppableView(
    modifier: Modifier = Modifier,
    onDrag: (inBounds: Boolean, isDragging: Boolean) -> Unit,
    dragState: DragState,
    content: @Composable (BoxScope.(data: DragData?) -> Unit),
) {
    val dragPosition = dragState.dragPosition
    val dragOffset = dragState.dragOffset
    var inBounds by remember { mutableStateOf(false) }

    Box(modifier = modifier.onGloballyPositioned {
        it.boundsInWindow().let { rect ->
            inBounds = rect.contains(dragPosition + dragOffset)
        }
    }
    ) {
        val dragData = if (inBounds) dragState.dragData else null
        onDrag(inBounds, dragState.isDragging)
        content(dragData)
    }
}
