package com.example.polyscrabbleclient.game.domain

import com.example.polyscrabbleclient.game.model.BoardModel
import com.example.polyscrabbleclient.game.model.TileContent
import com.example.polyscrabbleclient.game.sources.Direction
import com.example.polyscrabbleclient.game.sources.PlacementSetting
import com.example.polyscrabbleclient.game.viewmodels.TileCoordinates

data class PlaceLetterSetting(
    val placementSetting: PlacementSetting,
    val word: String
)

typealias IsHorizontal = Boolean
typealias IsVertical = Boolean

object BoardCrawler {

    private var board: BoardModel? = null

    fun canPlaceLetter(): Boolean {
        return board?.transientTilesCoordinates?.isNotEmpty() == true
    }

    fun reset(newBoard: BoardModel) {
        board = newBoard
    }

    fun getPlacement(): PlaceLetterSetting? {
        val coordinates = board?.transientTilesCoordinates?.toMutableList()

        if (coordinates === null) {
            throw Exception("BoardCrawler -> getFormedWord -> board null")
        }

        if (coordinates.size == 0) {
            throw Exception("BoardCrawler -> getFormedWord -> coordinates size is 0")
        }

        if (!isPlacementValidWithBoard(coordinates)) {
            return null
        }

        val direction = getWordDirection(coordinates)
        if (direction === null) {
            return null
        }

        return getPlacementFromBoard(direction, coordinates)
    }

    private fun isPlacementValidWithBoard(coordinates: MutableList<TileCoordinates>): Boolean {
        if (isCenterTileEmpty()) {
            return false
        }

        if (isCenterTileTransient()) {
            return true
        }

        coordinates.forEach {
            if (hasPermanentNeighbor(it)) {
                return true
            }
        }

        return false
    }

    private fun isCenterTileEmpty(): Boolean {
        return board?.isCenterTileEmpty() == true
    }

    private fun isCenterTileTransient(): Boolean {
        return board?.isCenterTileTransient() == true
    }

    private fun hasPermanentNeighbor(tileCoordinates: TileCoordinates): Boolean {
        val isThereAPermanentLeftTile =
            isTherePermanentTile(tileCoordinates.row, tileCoordinates.column - 1)
        val isThereAPermanentRightTile =
            isTherePermanentTile(tileCoordinates.row, tileCoordinates.column + 1)
        val isThereAPermanentUpTile =
            isTherePermanentTile(tileCoordinates.row - 1, tileCoordinates.column)
        val isThereAPermanentDownTile =
            isTherePermanentTile(tileCoordinates.row + 1, tileCoordinates.column)
        return isThereAPermanentLeftTile || isThereAPermanentRightTile || isThereAPermanentUpTile || isThereAPermanentDownTile
    }

    private fun getWordDirection(coordinates: MutableList<TileCoordinates>): Direction? {
        val (isVertical, isHorizontal) =
            if (coordinates.size == 1) {
                getDirectionFromSingleNewLetter(coordinates.first())
            } else {
                getWordDirectionFromManyNewLetters(coordinates)
            }

        return if (isHorizontal) {
            Direction.Horizontal
        } else if (isVertical) {
            Direction.Vertical
        } else {
            null
        }
    }

    private fun getDirectionFromSingleNewLetter(singleCoordinates: TileCoordinates): Pair<IsVertical, IsHorizontal> {
        val isThereALeftTile = isThereTile(singleCoordinates.row, singleCoordinates.column - 1)
        val isThereARightTile = isThereTile(singleCoordinates.row, singleCoordinates.column + 1)
        val isThereAUpTile = isThereTile(singleCoordinates.row - 1, singleCoordinates.column)
        val isThereADownTile = isThereTile(singleCoordinates.row + 1, singleCoordinates.column)

        // Assumption : isVertical and isHorizontal ARE NOT mutually exclusive
        val isVertical = isThereAUpTile || isThereADownTile
        val isHorizontal = isThereALeftTile || isThereARightTile

        // Defaulting to Direction.Horizontal
        return Pair(isVertical, isHorizontal)
    }

    private fun getWordDirectionFromManyNewLetters(coordinates: MutableList<TileCoordinates>): Pair<IsVertical, IsHorizontal> {
        val firstCoordinates = coordinates.first()
        val secondCoordinates = coordinates.last()

        // Assumption : isVertical and isHorizontal ARE mutually exclusive
        val isVertical = firstCoordinates.column == secondCoordinates.column
        val isHorizontal = firstCoordinates.row == secondCoordinates.row
        return Pair(isVertical, isHorizontal)
    }

    private fun getPlacementFromBoard(
        direction: Direction,
        coordinates: MutableList<TileCoordinates>
    ): PlaceLetterSetting? {
        val pivotCoordinates = coordinates.removeLast()
        val forwardSubWord = getPartOfTheWord(pivotCoordinates, coordinates, direction, true)
        val backwardSubWord = getPartOfTheWord(pivotCoordinates, coordinates, direction, false)

        val areOtherTilesConnected = forwardSubWord !== null || backwardSubWord !== null
        if (!areOtherTilesConnected) {
            return null
        }

        val areAllTransientTilesOnSameAxis = coordinates.isEmpty()
        if (!areAllTransientTilesOnSameAxis) {
            return null
        }

        val pivotLetter = getTileSafely(pivotCoordinates.row, pivotCoordinates.column)?.letter
        val placementSetting = getPlacementSetting(pivotCoordinates, backwardSubWord, direction)
        return PlaceLetterSetting(
            placementSetting = placementSetting,
            word = mergeWord(pivotLetter, forwardSubWord, backwardSubWord)
        )
    }

    private fun getPlacementSetting(
        pivotCoordinates: TileCoordinates,
        backwardSubWord: String?,
        direction: Direction
    ): PlacementSetting {
        val startCoordinates =
            if (backwardSubWord !== null && backwardSubWord.isNotEmpty()) {
                getNextCoordinates(pivotCoordinates, direction, false, backwardSubWord.length)
            } else {
                pivotCoordinates
            }
        return PlacementSetting(startCoordinates.column, startCoordinates.row, direction)
    }

    private fun mergeWord(
        pivotLetter: Char?,
        forwardSubWord: String?,
        backwardSubWord: String?
    ): String {
        return "${backwardSubWord?.reversed() ?: ""}${pivotLetter ?: ""}${forwardSubWord ?: ""}"
    }

    private fun getNextCoordinates(
        currentCoordinates: TileCoordinates,
        direction: Direction,
        isLookingForward: Boolean,
        numberOfSteps: Int = 1
    ): TileCoordinates {
        var nextRow = currentCoordinates.row
        var nextColumn = currentCoordinates.column

        if (direction == Direction.Horizontal) {
            if (isLookingForward) {
                nextColumn += numberOfSteps
            } else {
                nextColumn -= numberOfSteps
            }
        } else {
            if (isLookingForward) {
                nextRow += numberOfSteps
            } else {
                nextRow -= numberOfSteps
            }
        }

        return TileCoordinates(nextRow, nextColumn)
    }

    private fun getPartOfTheWord(
        pivotCoordinates: TileCoordinates,
        coordinates: MutableList<TileCoordinates>,
        direction: Direction,
        isLookingForward: Boolean
    ): String? {
        var currentCoordinates = getNextCoordinates(pivotCoordinates, direction, isLookingForward)
        var currentTile = getTileSafely(currentCoordinates)
        var subWord = ""

        while (currentTile !== null) {
            subWord += currentTile.letter
            if (coordinates.contains(currentCoordinates)) {
                coordinates.remove(currentCoordinates)
            }
            currentCoordinates = getNextCoordinates(currentCoordinates, direction, isLookingForward)
            currentTile = getTileSafely(currentCoordinates)
        }

        return subWord.ifEmpty { null }
    }

    private fun getTileSafely(tileCoordinates: TileCoordinates): TileContent {
        return getTileSafely(tileCoordinates.row, tileCoordinates.column)
    }

    private fun getTileSafely(row: Int, column: Int): TileContent {
        if (board === null) {
            return null
        }

        if (!board!!.isInsideOfBoard(column, row)) {
            return null
        }

        return board!![column, row]
    }

    private fun isThereTile(row: Int, column: Int): Boolean {
        return getTileSafely(row, column) !== null
    }

    private fun isTherePermanentTile(row: Int, column: Int): Boolean {
        if (board === null) {
            return false
        }

        if (board!!.transientTilesCoordinates.contains(TileCoordinates(row, column))) {
            return false
        }

        return isThereTile(row, column)
    }
}
