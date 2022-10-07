package com.example.polyscrabbleclient.game.model

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

    operator fun get(column: Int, row: Int): TileModel? {
        requireBoardIndexes(column, row)
        return tileGrid[column-1][row-1].content.value
    }

    operator fun get(column: Int, row: RowChar): TileModel? {
        return get(column, row.ordinal + 1)
    }

    operator fun set(column: Int, row: Int, tile: TileModel?) {
        requireBoardIndexes(column, row)
        tileGrid[column-1][row-1].content.value = tile
    }

    operator fun set(column: Int, row: RowChar, tile: TileModel?) {
        set(column, row.ordinal + 1, tile)
    }

    private fun requireBoardIndexes(column: Int, row: Int) {
        require(column in BoardRange && row in BoardRange)
        {
            "column and row indexes should be in range $BoardRange (found row=$row and column=$column)"
        }
    }
}
