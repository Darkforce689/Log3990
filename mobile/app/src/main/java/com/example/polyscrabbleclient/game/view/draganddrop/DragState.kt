package com.example.polyscrabbleclient.game.view.draganddrop

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset

// ADAPTED FROM https://github.com/microsoft/surface-duo-compose-sdk/blob/main/DragAndDrop/library/src/main/java/com/microsoft/device/dualscreen/draganddrop/DragContainer.kt
class DragState {
    var isDragging: Boolean by mutableStateOf(false)
    // TODO : MERGE LOCAL AND GLOBAL ?
    var dragGlobalPosition by mutableStateOf(Offset.Zero)
    var dragLocalPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var draggableView by mutableStateOf<(@Composable () -> Unit)?>(null)
    var draggableContent by mutableStateOf<DraggableContent?>(null)
    var onDropCallbacks = arrayListOf<() -> Unit>()

    fun onDragStart(
        isDragging: Boolean,
        dragGlobalPosition: Offset,
        draggableView: (@Composable () -> Unit)?,
        draggableContent: DraggableContent?
    ) {
        // TODO : REMOVE
        println("onDragStart")
        this.isDragging = isDragging
        this.dragGlobalPosition = dragGlobalPosition
        this.draggableView = draggableView
        this.draggableContent = draggableContent
    }

    fun onDragEnd() {
        // TODO : REMOVE
        println("onDragEnd")
        this.dragOffset = Offset.Zero
        this.isDragging = false
    }

    fun onDragCancel() {
        // TODO : REMOVE
        println("onDragCancel")
        this.dragOffset = Offset.Zero
        this.isDragging = false
    }

    fun onDrag(dragAmount: Offset) {
        // TODO : REMOVE
        println("onDrag")
        this.dragOffset += Offset(dragAmount.x, dragAmount.y)
    }

    fun onDrop() {
        // TODO : REMOVE
        println("onDrop")
        this.onDropCallbacks.forEach { callback -> callback() }
    }
}
