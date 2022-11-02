package com.example.polyscrabbleclient.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// FROM https://semicolonspace.com/jetpack-compose-circle-animation-gradient/
@Composable
fun SpinnerView(
    size: Dp = 40.dp,
    padding: Dp = 10.dp,
    slowness: Int = 600,

    circleColors: List<Color> = listOf(
        MaterialTheme.colors.background,
        MaterialTheme.colors.background,
        MaterialTheme.colors.secondary,
        MaterialTheme.colors.primary,
        MaterialTheme.colors.error,
        MaterialTheme.colors.error,
    ),
) {

    val infiniteTransition = rememberInfiniteTransition()

    val rotateAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = slowness,
                easing = LinearEasing
            )
        )
    )

    CircularProgressIndicator(
        modifier = Modifier
            .padding(padding)
            .size(size)
            .rotate(degrees = rotateAnimation)
            .border(
                width = 4.dp,
                brush = Brush.sweepGradient(circleColors),
                shape = CircleShape
            ),
        progress = 1f,
        strokeWidth = 1.dp,
        color = MaterialTheme.colors.background
    )
}

@Composable
fun SpinnerAnimationView() {
    Surface(
        color = MaterialTheme.colors.background
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpinnerView()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SpinnerPreview() {
    SpinnerAnimationView()
}

@Preview(showBackground = true)
@Composable
fun SpinnerPreviewWithPadding() {
    SpinnerView(padding = 10.dp)
}
