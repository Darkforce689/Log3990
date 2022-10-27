package com.example.polyscrabbleclient.game.view.draganddrop

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import com.example.polyscrabbleclient.game.model.TileModel

// ADAPTED FROM https://github.com/microsoft/surface-duo-compose-sdk/blob/main/DragAndDrop/library/src/main/java/com/microsoft/device/dualscreen/draganddrop/DragContainer.kt
class DragState {
    var isDragging: Boolean by mutableStateOf(false)
    var startingPosition by mutableStateOf(Offset.Zero)
    var offsetFromStartingPosition by mutableStateOf(Offset.Zero)
    var currentLocalPosition by mutableStateOf(Offset.Zero)
    var draggableView by mutableStateOf<(@Composable () -> Unit)?>(null)
    var draggableContent by mutableStateOf<DraggableContent?>(null)
    var onDropCallbacks = arrayListOf<() -> Unit>()

    fun onDragStart(
        startingPosition: Offset,
        draggableView: (@Composable () -> Unit)?,
        draggableContent: DraggableContent?
    ) {
        if (draggableContent?.canBeDragged?.invoke() == true) {
            this.isDragging = true
            this.startingPosition = startingPosition
            this.draggableView = draggableView
            this.draggableContent = draggableContent
        }
    }

    fun onDragEnd() {
        if (draggableContent?.canBeDragged?.invoke() == true) {
            this.offsetFromStartingPosition = Offset.Zero
            this.isDragging = false
        }
    }

    fun onDragCancel() {
        if (draggableContent?.canBeDragged?.invoke() == true) {
            this.offsetFromStartingPosition = Offset.Zero
            this.isDragging = false
        }
    }

    fun onDrag(dragAmount: Offset) {
        if (draggableContent?.canBeDragged?.invoke() == true) {
            this.offsetFromStartingPosition += Offset(dragAmount.x, dragAmount.y)
        }
    }

    fun onDrop() {
        if (draggableContent?.canBeDragged?.invoke() == true) {
            this.onDropCallbacks.forEach { callback -> callback() }
        }
    }
}
