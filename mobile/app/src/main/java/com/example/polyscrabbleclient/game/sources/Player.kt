package com.example.polyscrabbleclient.game.sources

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf


class Player(
    val name: String,
    val points: MutableState<Int> = mutableStateOf(0)
) {
    // TODO : List should not be initialized to default values
    var letters = mutableStateListOf('A', 'B', 'X', 'E', 'F', 'G', 'Z')
}
