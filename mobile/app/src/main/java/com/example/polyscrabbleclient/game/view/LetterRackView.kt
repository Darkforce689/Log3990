package com.example.polyscrabbleclient.game.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.polyscrabbleclient.game.model.TileModel
import com.example.polyscrabbleclient.game.view.draganddrop.DragState
import com.example.polyscrabbleclient.game.view.draganddrop.DraggableView
import com.example.polyscrabbleclient.game.viewmodels.LetterRackViewModel

@Composable
fun LetterRackView(
    dragState: DragState
) {
    val viewModel: LetterRackViewModel = viewModel()
    dragState.onDropActions.providerAction =
        { viewModel.markTileAsUsed(dragState.draggableContent) }

    dragState.onRaiseActions.rackAction =
        { viewModel.raiseTile(it) }

    Row(
        modifier = Modifier
            .background(MaterialTheme.colors.background),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        viewModel.game.userLetters.forEachIndexed { index, tile ->
            val getTile = { viewModel.game.userLetters[index] }
            tile.canBeDragged = viewModel.canBeDragged(getTile())
            val select = { tile.isSelected.value = !tile.isSelected.value }
            DraggableTileView(
                dragState,
                select,
            ) { getTile() }
        }
    }
}

@Composable
private fun DraggableTileView(
    dragState: DragState,
    select: () -> Unit,
    getTile: () -> TileModel,
) {
    DraggableView(
        dragState, getTile
    ) {
        TileView(getTile()) { select() }
    }
}
