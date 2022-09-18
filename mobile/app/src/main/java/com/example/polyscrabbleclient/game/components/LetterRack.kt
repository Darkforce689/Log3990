package com.example.polyscrabbleclient.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import com.example.polyscrabbleclient.game.viewmodels.LetterRackViewModel

@Preview(showBackground = true)
@Composable
fun LetterRack() {
    val viewModel: LetterRackViewModel = viewModel()
    val selectedLetters = remember {
        mutableStateListOf('B')
    }

    val isSelected = { letter: Char -> selectedLetters.contains(letter) }

    val clicked: (Char) -> Unit = { letter ->
        if (isSelected(letter)) {
            selectedLetters.remove(letter)
            println(selectedLetters.size)
        }
        else {
            selectedLetters.add(letter)
            println(selectedLetters.size)
        }
    }

    LazyRow(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(viewModel.letters.size) { index ->
            val letter = viewModel.letters[index]
            val onClick = { clicked(letter) }
            val isSelected by remember {
                mutableStateOf(selectedLetters.contains(letter))
            }
            Tile (
                letter,
                isSelected,
                onClick
            )
        }
    }
}
