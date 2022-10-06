package com.example.polyscrabbleclient.game.view

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
import com.example.polyscrabbleclient.game.viewmodels.LetterRackViewModel

@Composable
fun LetterRackView(navController: NavController) {
    val viewModel: LetterRackViewModel = viewModel()

    LazyRow(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom
    ) {
        items(viewModel.tiles.size) { index ->
            val tile = viewModel.tiles[index];
            val select = { tile.isSelected.value = !tile.isSelected.value }
            Tile(
                tile,
                select
            )
        }
    }
}
