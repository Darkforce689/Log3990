package com.example.polyscrabbleclient.game.domain

import com.example.polyscrabbleclient.game.model.TileModel


class LetterCreator {
    fun createTileFromLetter(letter: Char): TileModel {
        var points = 0;
        val letterIndex = letter.uppercaseChar().code - 'A'.code;
        if(letterIndex >= 0 && letterIndex < letterValues.size) {
            points = letterValues[letterIndex]
        }
        return TileModel(letter, points)
    }

    companion object{
        val letterValues = arrayListOf(1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 10, 1, 2, 1, 1, 3, 8, 1, 1, 1, 1, 4, 10, 10, 10, 10)
    }
}
