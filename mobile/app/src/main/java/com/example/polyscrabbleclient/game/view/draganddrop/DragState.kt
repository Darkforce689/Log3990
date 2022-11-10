package com.example.polyscrabbleclient.game.view.draganddrop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import com.example.polyscrabbleclient.game.model.TileModel
import com.example.polyscrabbleclient.game.view.TileView
import com.example.polyscrabbleclient.game.viewmodels.TileCoordinates


data class DropActions(
    var providerAction: (() -> DraggableContent?)? = null,
    var receiverAction: ((DraggableContent) -> Unit)? = null
)

data class RaiseActions(
    var boardAction: ((TileCoordinates) -> Unit)? = null,
    var rackAction: ((DraggableContent) -> Boolean)? = null
)

// ADAPTED FROM https://github.com/microsoft/surface-duo-compose-sdk/blob/main/DragAndDrop/library/src/main/java/com/microsoft/device/dualscreen/draganddrop/DragContainer.kt
object DragState {
    var isDragging: Boolean by mutableStateOf(false)
    var startingPosition by mutableStateOf(Offset.Zero)
    var offsetFromStartingPosition by mutableStateOf(Offset.Zero)
    var currentLocalPosition by mutableStateOf(Offset.Zero)
    var draggableView by mutableStateOf<(@Composable () -> Unit)?>(null)
    var draggableContent by mutableStateOf<DraggableContent?>(null)
    var onDropActions = DropActions()
    var onRaiseActions = RaiseActions()

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
            // RECEIVER and PROVIDER should be atomic actions (all or none)
            val draggableContent = this.onDropActions.providerAction?.invoke()
            if (draggableContent === null) {
                return
            }
            this.onDropActions.receiverAction?.invoke(draggableContent)
            reset()
        }
    }

    private fun reset() {
        this.currentLocalPosition = Offset.Zero
        this.startingPosition = Offset.Zero
        this.offsetFromStartingPosition = Offset.Zero
        this.draggableContent = null
        this.draggableView = null
        this.isDragging = false
    }

    fun onRaise(
        coordinates: TileCoordinates,
        draggableContent: DraggableContent,
        startPosition: Offset,
    ) {
        this.onRaiseActions.rackAction?.let {
            val canBeRaised = it(draggableContent)
            if (!canBeRaised) {
                return
            }

            this.onDragStart(
                startPosition,
                { TileView(tileModel = draggableContent as TileModel) {} },
                draggableContent,
            )

            this.onRaiseActions.boardAction?.invoke(coordinates)
        }
    }

    fun cancelDrop() {
        if (draggableContent is TileModel) {
            (draggableContent as TileModel).isUsedOnBoard.value = false
        }
        reset()
    }
}
