package com.example.polyscrabbleclient.game.view.draganddrop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.IntOffset
import com.example.polyscrabbleclient.game.view.DragState
import kotlin.math.roundToInt

@Composable
fun DraggedShadow(
    alpha: Float = 0.4f,
    dragState: DragState
) {
    val offset = dragState.dragPosition
    Box(
        modifier = Modifier
            .alpha(alpha)
            .offset
                {
                    println(offset.x)
                    IntOffset(offset.x.roundToInt(), offset.y.roundToInt())
                }
    ) {
        dragState.draggableContent?.let {
            println(it.toString())
            it()
        }
    }
}
