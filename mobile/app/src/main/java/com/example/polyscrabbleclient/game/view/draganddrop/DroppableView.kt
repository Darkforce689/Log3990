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
    onDrag: (inBounds: Boolean, isDragging: Boolean) -> Unit,
    dragState: DragState,
    content: @Composable (BoxScope.() -> Unit),
) {
    val dragPosition = dragState.dragGlobalPosition
    val dragOffset = dragState.dragOffset
    var inBounds by remember { mutableStateOf(false) }
    var a by remember { mutableStateOf(Offset.Zero) }
    var canvas by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = modifier.onGloballyPositioned {
        it.boundsInWindow().let { rect ->
            canvas = rect.topLeft
            dragState.dragLocalPosition = dragPosition + dragOffset - rect.topLeft
            a = dragPosition + dragOffset
            inBounds = rect.contains(a)
        }
    }
    ) {
        println("in:$inBounds pos:$dragPosition off:$dragOffset a:$a canvas:$canvas")
        onDrag(inBounds, dragState.isDragging)
        content()
    }
}
