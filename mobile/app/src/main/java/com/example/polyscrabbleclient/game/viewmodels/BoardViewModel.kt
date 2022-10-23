package com.example.polyscrabbleclient.game.viewmodels

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.game.domain.TileCreator
import com.example.polyscrabbleclient.game.model.BoardModel
import com.example.polyscrabbleclient.game.model.BoardRange
import com.example.polyscrabbleclient.game.model.TileModel
import com.example.polyscrabbleclient.game.sources.GameRepository
import com.example.polyscrabbleclient.game.view.ThickDividerWidth
import com.example.polyscrabbleclient.game.view.draganddrop.DraggableContent
import com.example.polyscrabbleclient.game.view.draganddrop.DraggableContentType

data class TileCoordinates (
    val row: Int,
    val column: Int
)

class BoardViewModel : ViewModel() {
    var board = GameRepository.game.board

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
    }

    // TODO : REMOVE
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

    fun drop(draggableContent: DraggableContent?) {
        val column = hoveredTileCoordinates?.column
        val row = hoveredTileCoordinates?.row
        if (draggableContent == null || column == null || row == null) {
            return
        }
        if (draggableContent.type !== DraggableContentType.TileModel) {
            return
        }
        board[column, row] = draggableContent as TileModel
    }

    fun canPlaceTile(tileCoordinates: TileCoordinates? = hoveredTileCoordinates): Boolean {
        if (tileCoordinates === null) {
            return false
        }
        board.debugPrint()
        return board[tileCoordinates] === null
    }
}
