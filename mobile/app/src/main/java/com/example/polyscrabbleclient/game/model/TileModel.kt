package com.example.polyscrabbleclient.game.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class TileModel (
    val letter: Char,
    val points: Int,
    var isSelected: MutableState<Boolean> = mutableStateOf(false)
)