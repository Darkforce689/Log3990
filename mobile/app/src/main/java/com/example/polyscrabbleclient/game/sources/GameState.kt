package com.example.polyscrabbleclient.game.sources


interface Letter {
    val char: String
    val value: Int
}

data class LightPlayer (
    val name: String,
    val points: Int,
    val letterRack: ArrayList<Letter>,
)

data class GameState (
//    val players: ArrayList<LightPlayer>,
    val activePlayerIndex: Int,
//    val grid: ArrayList<ArrayList<Letter>>,
//    val grid: ArrayList<ArrayList<Tile>>,
//    val lettersRemaining: Int,
//    val isEndOfGame: Boolean,
//    val winnerIndex: ArrayList<Int>,
): OnEventContent()

data class JoinGame (
    val game: Int,
): EmitEventContent()

