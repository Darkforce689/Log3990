package com.example.polyscrabbleclient.game.view.draganddrop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import com.example.polyscrabbleclient.game.domain.TileCreator
import com.example.polyscrabbleclient.game.view.TileView
import kotlin.math.roundToInt

@Composable
fun DragShadow(
    alpha: Float = 0.5f,
    dragState: DragState
) {
    val offset = dragState.dragGlobalPosition
    Box(
        modifier = Modifier
            .alpha(alpha)
            .offset
            {
                IntOffset(offset.x.roundToInt(), offset.y.roundToInt())
            }
    ) {
        dragState.draggableContent?.let {
            println(it.toString())
            it()
        }
        TileView(
            tileModel = TileCreator().createTileFromLetter('A')
        )
        {}
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDragShadow() {
    val d = DragState()
    d.draggableContent = {
        TileView(
            tileModel = TileCreator().createTileFromLetter('A')
        )
        {}
    }
    DragShadow(dragState = d)
}
