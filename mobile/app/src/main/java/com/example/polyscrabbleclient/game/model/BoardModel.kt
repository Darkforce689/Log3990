package com.example.polyscrabbleclient.game.model

import com.example.polyscrabbleclient.game.viewmodels.TileCoordinates

typealias TileContent = TileModel?
typealias TileContainerRow = Array<GridTileModel>
typealias TileGrid = Array<TileContainerRow>

const val BoardDimension = 15
val BoardRange = 1..BoardDimension
enum class RowChar { A, B, C, D, E, F, G, H, I, J, K, L, M, N, O }

class BoardModel(
    var tileGrid: TileGrid = Array(BoardDimension) { Array(BoardDimension) { GridTileModel() } }
) {
    fun debugPrint() {
        for (row in tileGrid) {
            for(tile in row) {
                if (tile.content.value !== null) {
                    print(tile.content.value!!.letter)
                } else {
                    print("-")
                }
            }
            println()
        }
    }

    fun toggleTileHover(column: Int, row: Int) {
        requireBoardIndexes(column, row)
        val isHighlighted = tileGrid[row-1][column-1].isHighlighted;
        isHighlighted.value = !isHighlighted.value;
    }

    fun toggleTileHover(coordinates: TileCoordinates) {
        toggleTileHover(coordinates.column, coordinates.row)
    }

    fun setTileHover(column: Int, row: Int, isHighlighted: Boolean) {
        requireBoardIndexes(column, row)
        tileGrid[row-1][column-1].isHighlighted.value = isHighlighted
    }

    fun setTileHover(coordinates: TileCoordinates, isHighlighted: Boolean) {
        setTileHover(coordinates.column, coordinates.row, isHighlighted)
    }

    operator fun get(column: Int, row: Int): TileContent {
        requireBoardIndexes(column, row)
        return tileGrid[row-1][column-1].content.value
    }

    operator fun get(column: Int, row: RowChar): TileContent {
        return get(column, row.ordinal + 1)
    }

    operator fun get(tileCoordinates: TileCoordinates): TileContent {
        return get(tileCoordinates.column, tileCoordinates.row)
    }

    operator fun set(column: Int, row: Int, tile: TileContent) {
        requireBoardIndexes(column, row)
        tileGrid[row-1][column-1].content.value = tile
    }

    operator fun set(column: Int, row: RowChar, tile: TileContent) {
        set(column, row.ordinal + 1, tile)
    }

    operator fun set(tileCoordinates: TileCoordinates, tile: TileContent) {
        set(tileCoordinates.column, tileCoordinates.row, tile)
    }

    private fun requireBoardIndexes(column: Int, row: Int) {
        require(column in BoardRange && row in BoardRange)
        {
            "column and row indexes should be in range $BoardRange (found row=$row and column=$column)"
        }
    }
}
