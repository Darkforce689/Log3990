package com.example.polyscrabbleclient.game.domain

import com.example.polyscrabbleclient.game.model.Tile


class LetterCreator {
    private val letterValues = arrayListOf(1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 10, 1, 2, 1, 1, 3, 8, 1, 1, 1, 1, 4, 10, 10, 10, 10, 0)

    fun getTileFromLetter(letter: Char): Tile {
        val points = letterValues[letter.code - 'A'.code]
        return Tile(letter, points)
    }
}
