package com.example.polyscrabbleclient.game.viewmodels

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.game.domain.TileCreator
import com.example.polyscrabbleclient.game.model.BoardModel

class BoardViewModel : ViewModel() {
    var board: BoardModel = BoardModel()
    var tileCreator = TileCreator()

    fun updateBoard() {
        // TODO : REMOVE + UPDATE LATER
        board[4, 5] = tileCreator.createTileFromLetter('A')
        board.toggleTileHover(4,5)
        board.toggleTileHover(4,6)
    }
}
