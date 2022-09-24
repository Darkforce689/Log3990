package com.example.polyscrabbleclient.game.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

typealias Tile = TileModel?
typealias TileContainer = MutableState<Tile>
typealias TileContainerRow = Array<TileContainer>
typealias TileGrid = Array<TileContainerRow>

const val BoardDimension = 16
val BoardRange = 0 until BoardDimension

class BoardModel(
    private var tileGrid: TileGrid = Array(BoardDimension) { Array(BoardDimension) { mutableStateOf(null) } }
) {
    fun debugPrint() {
        for(row in tileGrid) {
            for(tile in row) {
                print(tile.toString())
            }
            println()
        }
    }

    operator fun get(column: Int, row: Int): Tile {
        requireBoardIndexes(column, row)
        return tileGrid[column][row].value
    }

    operator fun set(column: Int, row: Int, tile: Tile) {
        requireBoardIndexes(column, row)
        tileGrid[column][row].value = tile
    }

    private fun requireBoardIndexes(column: Int, row: Int) {
        require(column in BoardRange && row in BoardRange)
        {
            "column and row indexes should be in range $BoardRange (found row=$row and column=$column)"
        }
    }
}
