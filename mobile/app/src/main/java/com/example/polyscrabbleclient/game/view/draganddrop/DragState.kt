package com.example.polyscrabbleclient.game.view.draganddrop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

// ADAPTED FROM https://github.com/microsoft/surface-duo-compose-sdk/blob/main/DragAndDrop/library/src/main/java/com/microsoft/device/dualscreen/draganddrop/DragContainer.kt
class DragState {
    var isDragging: Boolean by mutableStateOf(false)
    // TODO : MERGE LOCAL AND GLOBAL ?
    var dragGlobalPosition by mutableStateOf(Offset.Zero)
    var dragLocalPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var draggableContent by mutableStateOf<(@Composable () -> Unit)?>(null)

    fun onDragStart(isDragging: Boolean, dragGlobalPosition: Offset, draggableContent: (@Composable () -> Unit)?) {
        println("onDragStart")
        this.isDragging = isDragging
        this.dragGlobalPosition = dragGlobalPosition
        this.draggableContent = draggableContent
    }

    fun onDragEnd() {
        println("onDragEnd")
        this.dragOffset = Offset.Zero
        this.isDragging = false
    }

    fun onDragCancel() {
        println("onDragCancel")
        this.dragOffset = Offset.Zero
        this.isDragging = false
    }

    fun onDrag(dragAmount: Offset) {
        println("onDrag")
        this.dragOffset += Offset(dragAmount.x, dragAmount.y)
    }
}
