package com.example.polyscrabbleclient.game.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Tile(
    letter: Char,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val targetColor by animateColorAsState(
        targetValue =
            if (isSelected)
                MaterialTheme.colors.primary
            else
                Color.Transparent,
        animationSpec = tween(durationMillis = 500)
    )
    Surface(color = targetColor) {
        Text(
            text = "$letter",
            modifier = Modifier
                .selectable(selected = isSelected, onClick = onClick)
                .padding(32.dp)
        )
    }
}
