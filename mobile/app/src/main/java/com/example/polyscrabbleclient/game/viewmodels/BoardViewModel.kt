package com.example.polyscrabbleclient.game.viewmodels

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.game.domain.TileCreator
import com.example.polyscrabbleclient.game.model.BoardModel
import com.example.polyscrabbleclient.game.model.BoardRange
import com.example.polyscrabbleclient.game.view.ThickDividerWidth

class BoardViewModel : ViewModel() {
    var board: BoardModel = BoardModel()
    private var tileCreator = TileCreator()

    fun touchBoard(gridDivisionSize: Float, tapOffset: Offset) {
        // Dividers 0 and 1 are larger than the rest of the Grid
        val gridX = tapOffset.x + ThickDividerWidth
        val gridY = tapOffset.y + ThickDividerWidth
        if (gridX < 0 || gridY < 0) {
            return
        }
        val column = (gridX / gridDivisionSize).toInt()
        val row = (gridY / gridDivisionSize).toInt()
        if (row in BoardRange && column in BoardRange) {
            board.toggleTileHover(column, row)
        }
    }

    fun updateBoard() {
        // TODO : REMOVE + UPDATE LATER
        board[4, 5] = tileCreator.createTileFromLetter('A')
        board.toggleTileHover(2,2)
        board.toggleTileHover(4,5)
        board.toggleTileHover(4,6)
    }
}
