package com.example.polyscrabbleclient.game.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

typealias Tile = TileModel?
typealias TileContainer = MutableState<Tile>
typealias TileContainerRow = Array<TileContainer>
typealias TileGrid = Array<TileContainerRow>

const val BoardDimension = 15
val BoardRange = 1..BoardDimension
enum class RowChar { A, B, C, D, E, F, G, H, I, J, K, L, M, N, O }

class BoardModel(
    var tileGrid: TileGrid = Array(BoardDimension) { Array(BoardDimension) { mutableStateOf(null) } }
) {
    fun debugPrint() {
        for (row in tileGrid) {
            for(tile in row) {
                print(tile.toString())
            }
            println()
        }
    }

    operator fun get(column: Int, row: Int): Tile {
        requireBoardIndexes(column, row)
        return tileGrid[column-1][row-1].value
    }

    operator fun get(column: Int, row: RowChar): Tile {
        return get(column, row.ordinal + 1)
    }

    operator fun set(column: Int, row: Int, tile: Tile) {
        requireBoardIndexes(column, row)
        tileGrid[column-1][row-1].value = tile
    }

    operator fun set(column: Int, row: RowChar, tile: Tile) {
        set(column, row.ordinal + 1, tile)
    }

    private fun requireBoardIndexes(column: Int, row: Int) {
        require(column in BoardRange && row in BoardRange)
        {
            "column and row indexes should be in range $BoardRange (found row=$row and column=$column)"
        }
    }
}
