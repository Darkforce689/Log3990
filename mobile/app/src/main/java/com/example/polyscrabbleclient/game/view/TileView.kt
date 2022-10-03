package com.example.polyscrabbleclient.game.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.polyscrabbleclient.game.model.TileModel

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
                MaterialTheme.colors.primary
            else
                Color.Transparent,
        animationSpec = tween(durationMillis = 500)
    )
    Surface(
        color = targetColor,
        modifier = Modifier.border(width = 8.dp, MaterialTheme.colors.secondary)
    ) {
        Text(modifier = Modifier
            .selectable(
                selected = tileModel.isSelected.value,
                onClick = select
            )
            .padding(32.dp),
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
