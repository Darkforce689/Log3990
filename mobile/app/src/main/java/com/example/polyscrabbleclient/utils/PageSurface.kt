package com.example.polyscrabbleclient.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.example.polyscrabbleclient.getAssetsId

class Background(val name: String) {
    companion object {
        val Home = Background("home_bg")
        val Page = Background("classic_bg")
        val Game = Background("game_bg")
    }
}

@Composable
fun PageSurface(
    background: Background? = Background.Page,
    content: @Composable () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        if (background !== null) {
            Image(
                painterResource(id = getAssetsId(name = background.name)),
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
            )
        }
        content()
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun PageSurfacePreview() {
    PageSurface {}
}
