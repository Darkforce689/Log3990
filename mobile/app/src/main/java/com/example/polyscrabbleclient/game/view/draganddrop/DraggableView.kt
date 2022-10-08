package com.example.polyscrabbleclient.game.view.draganddrop

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import com.example.polyscrabbleclient.game.view.DragData
import com.example.polyscrabbleclient.game.view.DragState

// FROM https://github.com/microsoft/surface-duo-compose-sdk/blob/main/DragAndDrop/library/src/main/java/com/microsoft/device/dualscreen/draganddrop/DragContainer.kt
@Composable
fun DraggableView(
    dragData: DragData,
    dragState: DragState,
    content: @Composable () -> Unit,
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
                        println("onDragStart")
                        dragState.dragData = dragData
                        dragState.isDragging = true
                        dragState.dragPosition = currentPosition + it
                        dragState.draggableContent = content
                        println(dragState.draggableContent.toString())
                    },
                    onDrag = { change, dragAmount ->
                        println("onDrag")
                        change.consumeAllChanges()
                        dragState.dragOffset += Offset(dragAmount.x, dragAmount.y)
                    },
                    onDragEnd = {
                        println("onDragEnd")
                        dragState.dragOffset = Offset.Zero
                        dragState.isDragging = false
                    },
                    onDragCancel = {
                        println("onDragCancel")
                        dragState.dragOffset = Offset.Zero
                        dragState.isDragging = false
                    }
                )
            }
    ) {
        content()
    }
}
