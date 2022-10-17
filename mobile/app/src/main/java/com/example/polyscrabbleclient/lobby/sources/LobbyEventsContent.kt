package com.example.polyscrabbleclient.lobby.sources

import com.google.gson.annotations.SerializedName

enum class GameMode (val value: String) {
    @SerializedName("classic") Classic("classic"),
    @SerializedName("magic") Magic("magic"),
}

enum class BotDifficulty (val value: String) {
    @SerializedName("Facile") Easy("Facile"),
    @SerializedName("Expert") Expert("Expert"),
}

// Data class inheritance leads to unexpected behaviors
data class OnlineGameSettings (
    val gameMode: GameMode,
    val timePerTurn: Int,
    val playerNames: ArrayList<String>,
    val randomBonus: Boolean,
    val botDifficulty: BotDifficulty,
    val numberOfPlayers: Int,
)

data class OnlineGameSettingsUI (
    val id: String,
    val gameMode: GameMode,
    val timePerTurn: Int,
    val playerNames: ArrayList<String>,
    val randomBonus: Boolean,
    val botDifficulty: BotDifficulty,
    val numberOfPlayers: Int,
)


// Warning : Events Data Classes have to match the backend corresponding interfaces

typealias GameJoined = OnlineGameSettings

typealias GameStarted = OnlineGameSettings

typealias PendingGames = Array<OnlineGameSettingsUI>

typealias PendingGameId = GameToken

typealias Error = String

typealias CreateGame = OnlineGameSettings

typealias LaunchGame = GameToken

typealias JoinGame = GameToken

typealias GameToken = String
