package com.example.polyscrabbleclient.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.polyscrabbleclient.game.model.Tile
import com.example.polyscrabbleclient.game.viewmodels.LetterRackViewModel

@Composable
fun LetterRack(navController: NavController) {
    val viewModel: LetterRackViewModel = viewModel()
    val selectedTiles = remember {
        mutableStateMapOf<Tile, Boolean>()
    }

    viewModel.tiles.forEach { selectedTiles[it] = false }

    val isSelected: (Tile) -> Boolean = { tile: Tile -> selectedTiles[tile] == true }

    val clicked: (Tile) -> Unit = { tile ->
        if(selectedTiles.contains(tile)) {
            selectedTiles[tile] = !selectedTiles[tile]!!
        }
    }

    LazyRow(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom
    ) {
        items(viewModel.tiles.size) { index ->
            val tile = viewModel.tiles[index]
            val onClick = { clicked(tile) }

            Tile(
                tile,
                isSelected(tile),
                onClick
            )
        }
    }
}
