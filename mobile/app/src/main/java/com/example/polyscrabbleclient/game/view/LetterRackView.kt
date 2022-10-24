package com.example.polyscrabbleclient.game.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.polyscrabbleclient.game.view.draganddrop.DragState
import com.example.polyscrabbleclient.game.view.draganddrop.DraggableView
import com.example.polyscrabbleclient.game.viewmodels.LetterRackViewModel

@Composable
fun LetterRackView(
    dragState: DragState
) {
    val viewModel: LetterRackViewModel = viewModel()
    dragState.onDropCallbacks.add { viewModel.removeTile(dragState.draggableContent) }

    LazyRow(
        modifier = Modifier
            .background(MaterialTheme.colors.background),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        val tiles = viewModel.getTiles()
        items(tiles.size) { index ->
            val tile = tiles[index]
            val select = { tile.isSelected.value = !tile.isSelected.value }
            DraggableView(
                dragState,
                { tiles[index] },
            ) {
                TileView(tile, select)
            }
        }
    }
}
