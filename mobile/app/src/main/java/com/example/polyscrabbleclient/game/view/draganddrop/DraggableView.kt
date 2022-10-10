package com.example.polyscrabbleclient.game.view.draganddrop

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned

// ADAPTED FROM https://github.com/microsoft/surface-duo-compose-sdk/blob/main/DragAndDrop/library/src/main/java/com/microsoft/device/dualscreen/draganddrop/DragContainer.kt
@Composable
fun DraggableView(
    dragState: DragState,
    content: DraggableContent,
    contentView: @Composable () -> Unit,
) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    Box(
        modifier = Modifier
            .onGloballyPositioned {
                currentPosition = it.localToWindow(Offset.Zero)
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        dragState.onDragStart(
                            currentPosition + it,
                            contentView,
                            content
                        )
                    },
                    onDrag = { change, dragAmount ->
                        change.consumeAllChanges()
                        dragState.onDrag(dragAmount)
                    },
                    onDragEnd = { dragState.onDragEnd() },
                    onDragCancel = { dragState.onDragCancel() },
                )
            }
    ) {
        contentView()
    }
}
