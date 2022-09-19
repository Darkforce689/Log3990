package com.example.polyscrabbleclient.game.components

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
import com.example.polyscrabbleclient.game.model.Tile

@Preview(showBackground = true)
@Composable
fun Tile(
    tile: Tile = Tile('A', 1),
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val targetColor by animateColorAsState(
        targetValue =
            if (isSelected)
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
            .selectable(selected = isSelected, onClick = onClick)
            .padding(32.dp),
            text = buildAnnotatedString {
                append(tile.letter)
                withStyle(
                    SpanStyle(
                        baselineShift = BaselineShift.Subscript,
                        fontSize = 16.sp,
                        color = targetColor
                    )
                ) {
                    append(tile.point.toString())
                }
            }
        )
    }
}
