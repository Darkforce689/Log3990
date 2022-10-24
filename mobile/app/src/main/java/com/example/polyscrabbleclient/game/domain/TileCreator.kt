package com.example.polyscrabbleclient.game.domain

import com.example.polyscrabbleclient.game.model.TileModel
import com.example.polyscrabbleclient.game.sources.Tile


object TileCreator {
    private val letterValues = arrayListOf(
        1,
        3,
        3,
        2,
        1,
        4,
        2,
        4,
        1,
        8,
        10,
        1,
        2,
        1,
        1,
        3,
        8,
        1,
        1,
        1,
        1,
        4,
        10,
        10,
        10,
        10
    )

    fun createTileFromLetter(letter: Char): TileModel {
        var points = 0;
        val letterIndex = letter.uppercaseChar().code - 'A'.code;
        if (letterIndex >= 0 && letterIndex < letterValues.size) {
            points = letterValues[letterIndex]
        }
        return TileModel(letter, points)
    }

    fun createTileFromRawTile(tile: Tile): TileModel? {
        val isNullTile =
            tile.letterObject.char.length != 1 ||
                tile.letterObject.char[0] == ' '
        return if (isNullTile) {
            null
        } else {
            createTileFromLetter(tile.letterObject.char[0])
        }
    }
}
