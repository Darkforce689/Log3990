package com.example.polyscrabbleclient.game.viewmodels

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.game.domain.TileCreator
import com.example.polyscrabbleclient.game.model.BoardDimension
import com.example.polyscrabbleclient.game.model.BoardModel
import com.example.polyscrabbleclient.game.view.LocalDimensions
import com.example.polyscrabbleclient.game.view.ThickDividerWidth
import kotlin.math.roundToInt

class BoardViewModel : ViewModel() {
    var board: BoardModel = BoardModel()
    var tileCreator = TileCreator()

    fun touchBoard(localDimensions: LocalDimensions, tapOffset: Offset) {
        val rawX = tapOffset.x
        val rawY = tapOffset.y

        val paddedX = rawX + localDimensions.boardPadding
        val paddedY = rawY + localDimensions.boardPadding

        // Dividers 0 and 1 are larger than the rest of the Grid
        val gridX = paddedX + 2 * ThickDividerWidth //- localDimensions.gridDivisionSize
        val gridY = paddedY + 2 * ThickDividerWidth //- localDimensions.gridDivisionSize

        if (gridX < 0 || gridY < 0) {
            return
        }

        val column = gridX / localDimensions.gridDivisionSize
        val row = gridY / localDimensions.gridDivisionSize
        println("x:$rawX, y:$rawY")
        println("row:$row, column:$column")
        board.toggleTileHover(column.toInt(), row.toInt())
    }

    fun updateBoard() {
        // TODO : REMOVE + UPDATE LATER
        board[4, 5] = tileCreator.createTileFromLetter('A')
        board.toggleTileHover(2,2)
        board.toggleTileHover(4,5)
        board.toggleTileHover(4,6)
    }
}
