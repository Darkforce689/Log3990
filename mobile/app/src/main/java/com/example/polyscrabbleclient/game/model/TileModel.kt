package com.example.polyscrabbleclient.game.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.polyscrabbleclient.game.view.draganddrop.DraggableContent
import com.example.polyscrabbleclient.game.view.draganddrop.DraggableContentType
import java.util.*

data class TileModel (
    val letter: Char,
    val points: Int,
    var isSelected: MutableState<Boolean> = mutableStateOf(false)
): DraggableContent(type = DraggableContentType.TileModel)

data class GridTileModel (
    val content: MutableState<TileModel?> = mutableStateOf(null),
    var isHighlighted: MutableState<Boolean> = mutableStateOf(false)
)
