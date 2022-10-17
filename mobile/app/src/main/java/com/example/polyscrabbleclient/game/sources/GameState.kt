package com.example.polyscrabbleclient.game.sources


data class Tile (
    val letterMultiplicator: Int,
    val wordMultiplicator: Int,
    val letterObject: Letter
)

data class Letter (
    val char: String,
    val value: Int
)

data class LightPlayer (
    val name: String,
    val points: Int,
    val letterRack: ArrayList<Letter>,
)

abstract class OnEventContent
abstract class EmitEventContent

// Warning : Events Data Classes have to match the backend corresponding interfaces
data class RemainingTime(val a: Int): OnEventContent()
data class StartTime(val a: Int): OnEventContent()
data class GameState (
    val players: ArrayList<LightPlayer>,
    val activePlayerIndex: Int,
    val grid: ArrayList<ArrayList<Tile>>,
    val lettersRemaining: Int,
    val isEndOfGame: Boolean,
    val winnerIndex: ArrayList<Int>,
): OnEventContent()
data class TransitionGameState(val a: Int): OnEventContent()

data class JoinGame(val a: Int): EmitEventContent()
data class NextAction(val a: Int): EmitEventContent()
data class Disconnect(val a: Int): EmitEventContent()
