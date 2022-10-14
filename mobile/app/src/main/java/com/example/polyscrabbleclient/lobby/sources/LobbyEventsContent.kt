package com.example.polyscrabbleclient.lobby.sources

enum class GameMode (val value: String) {
    Classic("classic"),
    Magic("magic"),
}

enum class BotDifficulty (val value: String) {
    Easy("Facile"),
    Expert("Expert"),
}

abstract class OnLobbyEventContent
abstract class EmitLobbyEventContent

// Warning : Events Data Classes have to match the backend corresponding interfaces
data class GameJoined(
    val todo: Any
): OnLobbyEventContent()

data class GameStarted(
    val todo: Any
): OnLobbyEventContent()

data class PendingGames (
    val todo: Any
): OnLobbyEventContent()

data class PendingGameId(
    val todo: Any
): OnLobbyEventContent()

data class Error(
    val todo: Any
): OnLobbyEventContent()


data class CreateGame(
    val gameMode: GameMode,
    val timePerTurn: Int,
    val playerNames: ArrayList<String>,
    val randomBonus: Boolean,
    val botDifficulty: BotDifficulty,
    val numberOfPlayers: Int,
): EmitLobbyEventContent()

data class LaunchGame(
    val todo: Any
): EmitLobbyEventContent()

data class JoinGame(
    val id: String,
    val name: String,
): EmitLobbyEventContent()
