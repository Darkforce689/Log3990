package com.example.polyscrabbleclient.game.sources

import androidx.annotation.Nullable
import com.google.gson.annotations.SerializedName

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

// TODO : REMOVE (ONLY TEMPORARY WHILE WAITING FOR !54)
data class UserAuth (
    val playerName: String,
    val gameToken: String
)

data class Position(
    val x: Int,
    val y: Int
)

enum class Direction (value:String) {
    @SerializedName("H") Horizontal("H"),
    @SerializedName("V") Vertical("V"),
}

data class PlacementSetting (
    val x: Int,
    val y: Int,
    val direction: Direction
)

data class OnlineAction (
    val type: OnlineActionType,
    val placementSettings: PlacementSetting? = null,
    val letters: String? = null,
    val letterRack: ArrayList<Letter>? = null,
    val position: Position? = null
)

// No Union types in Kotlin
enum class OnlineActionType (value: String) {
    // OnlineActionType
    @SerializedName("place") Place("place"),
    @SerializedName("exchange") Exchange("exchange"),
    @SerializedName("pass") Pass("pass"),

    // OnlineMagicCardActionType
    @SerializedName("splitPoints") SplitPoints("splitPoints"),
    @SerializedName("exchangeALetter") ExchangeALetter("exchangeALetter"),
    @SerializedName("placeBonus") PlaceBonus("placeBonus"),
    @SerializedName("exchangeHorse") ExchangeHorse("exchangeHorse"),
    // TODO : ADD OTHERS
}


// Warning : Events Data Classes have to match the backend corresponding interfaces

typealias RemainingTime = Int

typealias StartTime = Int

data class GameState (
    val players: ArrayList<LightPlayer>,
    val activePlayerIndex: Int,
    val grid: ArrayList<ArrayList<Tile>>,
    val lettersRemaining: Int,
    val isEndOfGame: Boolean,
    val winnerIndex: ArrayList<Int>,
)

// TODO
typealias TransitionGameState = Nullable

// TODO : UPDATE (ONLY TEMPORARY WHILE WAITING FOR !54)
typealias JoinGame = UserAuth

// TODO
typealias NextAction = OnlineAction

// TODO
typealias Disconnect = Nullable
