package com.example.polyscrabbleclient.game.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.polyscrabbleclient.game.view.draganddrop.DraggableContent
import com.example.polyscrabbleclient.game.view.draganddrop.DraggableContentType

data class TileModel(
    val letter: Char,
    val points: Int,
    var isSelected: MutableState<Boolean> = mutableStateOf(false),
    var isUsedOnBoard: MutableState<Boolean> = mutableStateOf(false)
) : DraggableContent(
    type = DraggableContentType.TileModel,
    canBeDragged = { !isUsedOnBoard.value }
)

data class GridTileModel(
    val content: MutableState<TileModel?> = mutableStateOf(null),
    var isHighlighted: MutableState<Boolean> = mutableStateOf(false),
    var letterMultiplier: Int= 1,
    var wordMultiplier: Int= 1,
)
