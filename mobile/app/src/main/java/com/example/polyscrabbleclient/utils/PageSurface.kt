package com.example.polyscrabbleclient.utils

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PageSurface(content: @Composable () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        content()
    }
}
