package com.example.polyscrabbleclient.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200,
    secondaryVariant = Teal200,
    background = Color(0xFF121212),
    surface = Color(0xFF121212),
    error = Color(0xFFCF6679),
    onPrimary = Black,
    onSecondary = Black,
    onBackground = White,
    onSurface = White,
    onError = Black
)

private val LightColorPalette = lightColors(
    primary = Color(0xFF6200EE),
    primaryVariant = Purple700,
    secondary = Color(0xFF03DAC6),
    secondaryVariant = Color(0xFF018786),
    background = White,
    surface = White,
    error = Color(0xFFB00020),
    onPrimary = White,
    onSecondary = Black,
    onBackground = Black,
    onSurface = Black,
    onError = White
)

val Colors.tileBackground: Color
    @Composable
    get() = if (isLight) LightTileBackgroundColor else DarkTileBackgroundColor

val Colors.transientTileBackground: Color
    @Composable
    get() = if (isLight) LightTransientTileBackgroundColor else DarkTransientTileBackgroundColor

val Colors.grayedOutTileBackground: Color
    @Composable
    get() = if (isLight) LightTileGrayedOutBackgroundColor else DarkTileGrayedOutBackgroundColor


@Composable
fun PolyScrabbleClientTheme(
    isDarkTheme: MutableState<Boolean>,
    content: @Composable () -> Unit
) {
    val colors = if (isDarkTheme.value) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

object NoRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = Color.Unspecified

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleAlpha(0.0f,0.0f,0.0f,0.0f)
}
