package com.example.polyscrabbleclient.game.viewmodels

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.game.domain.TileCreator
import com.example.polyscrabbleclient.game.model.BoardModel
import com.example.polyscrabbleclient.game.model.BoardRange
import com.example.polyscrabbleclient.game.view.ThickDividerWidth

data class TileCoordinates (
    val row: Int,
    val column: Int
)

class BoardViewModel : ViewModel() {
    var board: BoardModel = BoardModel()
    private var tileCreator = TileCreator()
    var hoveredTileCoordinates:TileCoordinates? = null

    fun hoverBoard(gridDivisionSize: Float, hoverOffset: Offset) {
        val newCoordinates = getTileFromLocalPosition(gridDivisionSize, hoverOffset)

        // Same coordinates means the hovered tile is still the previous hovered tile
        if (newCoordinates == hoveredTileCoordinates) {
            return
        }

        // un-hover previous tile (if applicable)
        unHoverPreviousCoordinates()

        // return if no new tile is hovered
        if (newCoordinates == null) {
            return
        }

        // hover new tile
        board.setTileHover(newCoordinates, true)

        // Update currently hovered tile coordinates
        hoveredTileCoordinates = newCoordinates
    }

    private fun unHoverPreviousCoordinates() {
        // Null previous coordinates -> no tile is being hovered
        if (hoveredTileCoordinates == null) {
            return
        }
        // Non-null previous coordinates -> un-hovered previous tile
        board.setTileHover(hoveredTileCoordinates!!, false)
        hoveredTileCoordinates = null
        return
    }

    fun touchBoard(gridDivisionSize: Float, tapOffset: Offset) {
        val coordinates = getTileFromLocalPosition(gridDivisionSize, tapOffset)
        if (coordinates != null && coordinates.row in BoardRange && coordinates.column in BoardRange) {
            board.toggleTileHover(coordinates.column, coordinates.row)
        }
    }

    private fun getTileFromLocalPosition(gridDivisionSize: Float, tapOffset: Offset): TileCoordinates? {
        // Dividers 0 and 1 are larger than the rest of the Grid
        val gridX = tapOffset.x + ThickDividerWidth
        val gridY = tapOffset.y + ThickDividerWidth
        println("getTileFromLocalPosition -> tapOffset:$tapOffset")
        if (gridX < 0 || gridY < 0) {
            return null
        }
        val column = (gridX / gridDivisionSize).toInt()
        val row = (gridY / gridDivisionSize).toInt()
        if (row in BoardRange && column in BoardRange) {
            return TileCoordinates(row, column)
        }
        return null
    }

    fun updateBoard() {
        // TODO : REMOVE + UPDATE LATER
        board[4, 5] = tileCreator.createTileFromLetter('A')
        board.toggleTileHover(2,2)
        board.toggleTileHover(4,5)
        board.toggleTileHover(4,6)
    }
}
