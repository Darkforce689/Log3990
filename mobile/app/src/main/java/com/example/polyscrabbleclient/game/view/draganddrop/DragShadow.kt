package com.example.polyscrabbleclient.game.view.draganddrop

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import com.example.polyscrabbleclient.game.domain.TileCreator
import com.example.polyscrabbleclient.game.view.TileView

// ADAPTED FROM https://github.com/microsoft/surface-duo-compose-sdk/blob/main/DragAndDrop/library/src/main/java/com/microsoft/device/dualscreen/draganddrop/DragContainer.kt
@Composable
fun DragShadow(
    dragState: DragState,
    shadowAlpha: Float = 0.5f,
) {
    if (dragState.isDragging) {
        var targetSize by remember {
            mutableStateOf(IntSize.Zero)
        }
        Box(
            modifier = Modifier
                .graphicsLayer {
                    alpha = shadowAlpha
                    val offset = dragState.startingPosition + dragState.offsetFromStartingPosition
                    translationX = offset.x.minus(targetSize.width / 2)
                    translationY = offset.y.minus(targetSize.height / 2)
                }
                .onGloballyPositioned {
                    targetSize = it.size
                }
        ) {
            dragState.draggableView?.invoke()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDragShadow() {
    val d = DragState
    d.isDragging = true
    d.startingPosition = Offset(100f, 100f)
    d.draggableView = {
        TileView(
            tileModel = TileCreator.createTileFromLetter('X')
        )
        {}
    }
    DragShadow(dragState = d)
}
