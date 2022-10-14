package com.example.polyscrabbleclient.lobby.sources

enum class GameMode (val value: String) {
    Classic("classic"),
    Magic("magic"),
}

enum class BotDifficulty (val value: String) {
    Easy("Facile"),
    Expert("Expert"),
}

// Warning : Events Data Classes have to match the backend corresponding interfaces
data class GameJoined(
    val todo: Any
)

data class GameStarted(
    val todo: Any
)

typealias PendingGames = Array<OnlineGameSettingsUI>
data class OnlineGameSettingsUI (
    val id : Int,
    val gameMode: GameMode,
    val timePerTurn: Int,
    val playerNames: ArrayList<String>,
    val randomBonus: Boolean,
    val botDifficulty: BotDifficulty,
    val numberOfPlayers: Int,
)

data class PendingGameId(
    val todo: Any
)

data class Error(
    val todo: Any
)

data class CreateGame(
    val gameMode: GameMode,
    val timePerTurn: Int,
    val playerNames: ArrayList<String>,
    val randomBonus: Boolean,
    val botDifficulty: BotDifficulty,
    val numberOfPlayers: Int,
)

data class LaunchGame(
    val todo: Any
)

data class JoinGame(
    val id: String,
    val name: String,
)
