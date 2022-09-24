package com.example.polyscrabbleclient.game.view

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.polyscrabbleclient.game.model.BoardModel
import com.example.polyscrabbleclient.game.viewmodels.BoardViewModel

@Composable
fun BoardView (
    boardModel: BoardModel = BoardModel(),
) {
    val viewModel: BoardViewModel = viewModel()


}
