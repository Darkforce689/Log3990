package com.example.polyscrabbleclient.game.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.polyscrabbleclient.game.model.TileModel
import com.example.polyscrabbleclient.ui.theme.TileBackgroundColor
import kotlin.math.roundToInt

val subscript = SpanStyle(
    baselineShift = BaselineShift.Subscript,
    fontSize = 16.sp,
)


@Composable
fun Tile(
    tileModel: TileModel,
    select: () -> Unit,
) {
    val targetColor by animateColorAsState(
        targetValue =
            if (tileModel.isSelected.value)
                MaterialTheme.colors.secondary
            else
                MaterialTheme.colors.primary,
        animationSpec = tween(durationMillis = 200)
    )

    var initialX = 0f
    var initialY = 0f

    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }

    Surface(
        color = TileBackgroundColor,
        modifier = Modifier
            .offset {
                IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt())
            }
            .pointerInput (Unit) {
                detectDragGestures(
                    onDrag = { change, offset ->
                        change.consumeAllChanges()
                        offsetX.value += offset.x
                        offsetY.value += offset.y
                    },
                    onDragStart = { offset ->
                        initialX = offset.x
                        initialY = offset.y
                    },
                    onDragEnd = {
                        offsetX.value = initialX
                        offsetY.value = initialY
                    },
                    onDragCancel = { println("onDragCancel") }
                )
            }
            .border(width = 4.dp, targetColor)
    ) {
        Text(modifier = Modifier
            .selectable(
                selected = tileModel.isSelected.value,
                onClick = select
            )
            .padding(16.dp),
            text = buildAnnotatedString {
                append(tileModel.letter)
                withStyle(subscript) {
                    append(tileModel.points.toString())
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Preview1 () {
    val tileModel = TileModel('A', 1)
    Tile(
        tileModel,
        select = { tileModel.isSelected.value = !tileModel.isSelected.value }
    )
}
@Preview(showBackground = true)
@Composable
fun Preview2 () {
    val tileModel = TileModel('B', 2)
    tileModel.isSelected.value = true
    Tile(
        tileModel,
        select = { tileModel.isSelected.value = !tileModel.isSelected.value }
    )
}
