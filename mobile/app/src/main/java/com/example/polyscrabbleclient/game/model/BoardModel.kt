package com.example.polyscrabbleclient.game.model

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
                print(tile.toString())
            }
            println()
        }
    }

    fun toggleTileHover(column: Int, row: Int) {
        requireBoardIndexes(column, row)
        val isHighlighted = tileGrid[row-1][column-1].isHighlighted;
        isHighlighted.value = !isHighlighted.value;
    }

    fun setTileHover(column: Int, row: Int, isHighlighted: Boolean) {
        requireBoardIndexes(column, row)
        tileGrid[row-1][column-1].isHighlighted.value = isHighlighted
    }

    operator fun get(column: Int, row: Int): TileContent {
        requireBoardIndexes(column, row)
        return tileGrid[row-1][column-1].content.value
    }

    operator fun get(column: Int, row: RowChar): TileContent {
        return get(column, row.ordinal + 1)
    }

    operator fun set(column: Int, row: Int, tile: TileContent) {
        requireBoardIndexes(column, row)
        tileGrid[row-1][column-1].content.value = tile
    }

    operator fun set(column: Int, row: RowChar, tile: TileContent) {
        set(column, row.ordinal + 1, tile)
    }

    private fun requireBoardIndexes(column: Int, row: Int) {
        require(column in BoardRange && row in BoardRange)
        {
            "column and row indexes should be in range $BoardRange (found row=$row and column=$column)"
        }
    }
}
