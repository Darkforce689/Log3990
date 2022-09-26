package com.example.polyscrabbleclient.game.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.polyscrabbleclient.game.domain.TileCreator
import com.example.polyscrabbleclient.game.model.RowChar
import com.example.polyscrabbleclient.game.model.TileModel
import com.example.polyscrabbleclient.game.viewmodels.BoardViewModel

@Composable
fun BoardView () {
    val viewModel: BoardViewModel = viewModel()

    viewModel.board[1, RowChar.A] = TileCreator().createTileFromLetter('A')
    Row {
        for(rows in viewModel.board.tileGrid) {
            Column {
                for(tileContainer in rows) {
                   val letter = tileContainer.value?.letter ?: '-'
                   Text(text = letter.toString())
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun BoardPreview() {
    BoardView()
}
