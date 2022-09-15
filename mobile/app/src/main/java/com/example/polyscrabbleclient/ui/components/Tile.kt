package com.example.polyscrabbleclient.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.polyscrabbleclient.ui.viewmodels.TileViewModel

@Preview(showBackground = true)
@Composable
fun Tile(letter: Char = 'X') {
    val tileViewModel: TileViewModel = viewModel()
    val targetColor by animateColorAsState(
        targetValue = if (tileViewModel.isSelected) MaterialTheme.colors.primary else Color.Green,
        animationSpec = tween(durationMillis = 2000)
    )
    Surface(color = targetColor) {
        Text(
            text = "$letter",
            modifier = Modifier
                .clickable { tileViewModel.clicked() }
                .padding(16.dp)
        )
    }
}
