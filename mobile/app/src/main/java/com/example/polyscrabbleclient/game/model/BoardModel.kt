package com.example.polyscrabbleclient.game.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.polyscrabbleclient.game.domain.TileCreator
import com.example.polyscrabbleclient.game.sources.GameRepository
import com.example.polyscrabbleclient.game.sources.IMagicCard
import com.example.polyscrabbleclient.game.sources.Position
import com.example.polyscrabbleclient.game.sources.Tile
import com.example.polyscrabbleclient.game.viewmodels.TileCoordinates
import com.example.polyscrabbleclient.lobby.sources.GameMode
import com.example.polyscrabbleclient.utils.constants.place_random_bonus_id

typealias TileContent = TileModel?
typealias TileContainerRow = Array<GridTileModel>
typealias TileGrid = Array<TileContainerRow>

const val BoardDimension = 15
val BoardRange = 1..BoardDimension
const val CenterIndex = BoardDimension / 2 + 1

enum class RowChar { A, B, C, D, E, F, G, H, I, J, K, L, M, N, O }

class BoardModel {
    var tileGrid: TileGrid = Array(BoardDimension) { Array(BoardDimension) { GridTileModel() } }
        private set
    var transientTilesCoordinates = mutableSetOf<TileCoordinates>()
    var selectedCoordinates: MutableState<TileCoordinates?> = mutableStateOf(null)

    val jokerModel: JokerModel = JokerModel(this)

    fun updateGrid(grid: ArrayList<ArrayList<Tile>>) {
        unselect()
        requireBoardDimensions(grid)
        for (row in BoardRange) {
            for (column in BoardRange) {
                val tile = grid[row - 1][column - 1]
                this[column, row] = TileCreator.createTileFromRawTile(tile)
                setMultipliers(column, row, tile.letterMultiplicator, tile.wordMultiplicator)
            }
        }
        transientTilesCoordinates.clear()
    }

    fun removeTransientTilesContent() {
        transientTilesCoordinates.toMutableList().forEach {
            this.setTransient(it, null)
        }
        transientTilesCoordinates.clear()
    }

    fun debugPrint() {
        for (row in tileGrid) {
            for (tile in row) {
                if (tile.content.value !== null) {
                    print(tile.content.value!!.displayedLetter)
                } else {
                    print("-")
                }
            }
            println()
        }
    }

    fun setSelected(column: Int, row: Int) {
        val model = GameRepository.model
        val cantSelectTile =
            model.gameMode.value != GameMode.Magic || !model.isActivePlayer() || !model.drawnMagicCards.value[model.getUserIndex()].contains(
                IMagicCard(
                    place_random_bonus_id
                )
            ) || (selectedCoordinates.value?.row == row && selectedCoordinates.value?.column == column)
        unselect()
        if (cantSelectTile) return
        tileGrid[row - 1][column - 1].isHighlighted.value = true
        selectedCoordinates.value = TileCoordinates(row, column)
    }

    // TODO: add a way to call unselect when clicking outside the canvas
    fun unselect() {
        val coordinates = selectedCoordinates.value ?: return
        val tile = tileGrid[coordinates.row - 1][coordinates.column - 1]
        tile.isHighlighted.value = false;
        selectedCoordinates.value = null
    }

    fun setTileHover(column: Int, row: Int, isHighlighted: Boolean) {
        requireBoardIndexes(column, row)
        tileGrid[row - 1][column - 1].isHighlighted.value = isHighlighted
        if (isHighlighted) {
            GameRepository.sendContinuousSync(
                arrayListOf(
                    Position(
                        column - 1,
                        row - 1
                    )

                )
            )
        } else {
            GameRepository.sendContinuousSync(
                arrayListOf()
            )
        }
    }

    fun setTileHover(coordinates: TileCoordinates, isHighlighted: Boolean) {
        setTileHover(coordinates.column, coordinates.row, isHighlighted)
    }

    operator fun get(column: Int, row: Int): TileContent {
        requireBoardIndexes(column, row)
        return tileGrid[row - 1][column - 1].content.value
    }

    operator fun get(column: Int, row: RowChar): TileContent {
        return get(column, row.ordinal + 1)
    }

    operator fun get(tileCoordinates: TileCoordinates): TileContent {
        return get(tileCoordinates.column, tileCoordinates.row)
    }

    operator fun set(column: Int, row: Int, tile: TileContent) {
        requireBoardIndexes(column, row)
        tileGrid[row - 1][column - 1].content.value = tile
    }

    operator fun set(column: Int, row: RowChar, tile: TileContent) {
        set(column, row.ordinal + 1, tile)
    }

    operator fun set(tileCoordinates: TileCoordinates, tile: TileContent) {
        set(tileCoordinates.column, tileCoordinates.row, tile)
    }

    private fun setMultipliers(
        column: Int,
        row: Int,
        letterMultiplicator: Int,
        wordMultiplicator: Int
    ) {
        tileGrid[row - 1][column - 1].letterMultiplier = letterMultiplicator
        tileGrid[row - 1][column - 1].wordMultiplier = wordMultiplicator
    }

    fun isInsideOfBoard(column: Int, row: Int): Boolean {
        return column in BoardRange && row in BoardRange
    }

    fun isInsideOfBoard(tilesCoordinates: TileCoordinates): Boolean {
        return isInsideOfBoard(tilesCoordinates.column, tilesCoordinates.row)
    }

    fun isCenterTileEmpty(): Boolean {
        return this[CenterIndex, CenterIndex] === null
    }

    fun isCenterTileTransient(): Boolean {
        return transientTilesCoordinates.contains(TileCoordinates(CenterIndex, CenterIndex))
    }

    fun setTransient(tileCoordinates: TileCoordinates, tile: TileContent) {
        markPreviousTileAsUnused(tileCoordinates)
        this[tileCoordinates] = tile
        if (tile === null) {
            transientTilesCoordinates.remove(tileCoordinates)
        } else {
            transientTilesCoordinates.add(tileCoordinates)
            jokerModel.checkOpenForJokerSelection(tile, tileCoordinates)
        }
        GameRepository.sendSync(transientTilesCoordinates)
    }

    private fun markPreviousTileAsUnused(tileCoordinates: TileCoordinates) {
        val previousTile = this[tileCoordinates]
        if (previousTile != null) {
            previousTile.isUsedOnBoard.value = false
        }
    }

    private fun requireBoardIndexes(column: Int, row: Int) {
        require(column in BoardRange && row in BoardRange)
        {
            "column and row indexes should be in range $BoardRange (found row=$row and column=$column)"
        }
    }

    private fun requireBoardDimensions(grid: ArrayList<ArrayList<Tile>>) {
        val rows = grid.size
        val columns = try {
            grid[0].size
        } catch (e: Exception) {
            -1
        }
        require(columns == BoardDimension && rows == BoardDimension)
        {
            "grid should be of size $BoardDimension (found rows=$rows and columns=$columns)"
        }
    }
}
