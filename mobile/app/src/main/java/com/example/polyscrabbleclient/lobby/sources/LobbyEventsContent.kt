package com.example.polyscrabbleclient.lobby.sources

import androidx.annotation.Nullable
import com.google.gson.annotations.SerializedName

enum class GameMode(val value: String) {
    @SerializedName("classic")
    Classic("classic"),

    @SerializedName("magic")
    Magic("magic"),
}

enum class BotDifficulty(val value: String) {
    @SerializedName("Facile")
    Easy("Facile"),

    @SerializedName("Expert")
    Expert("Expert"),
}

data class OnlineGameSettings(
    val id: String,
    val gameMode: GameMode,
    val timePerTurn: Int,
    val playerNames: ArrayList<String>,
    val tmpPlayerNames: ArrayList<String>,
    val privateGame: Boolean,
    val gameStatus: String,
    val randomBonus: Boolean,
    val botDifficulty: BotDifficulty,
    val numberOfPlayers: Int,
    val password: String,
)

typealias PendingGames = ArrayList<OnlineGameSettings>

typealias ObservableGames = ArrayList<OnlineGameSettings>

typealias PendingGameId = String

// Warning : Events Data Classes have to match the backend corresponding interfaces

typealias GameJoined = OnlineGameSettings

typealias GameStarted = OnlineGameSettings

data class PendingAndObservableGames(
    val pendingGamesSettings: PendingGames,
    val observableGamesSettings: ObservableGames
)

typealias Error = String

data class CreateGame (
    val gameMode: GameMode,
    val timePerTurn: Int,
    val playerNames: ArrayList<String>,
    val randomBonus: Boolean,
    val botDifficulty: BotDifficulty,
    val numberOfPlayers: Int,
)

typealias LaunchGame = PendingGameId

data class JoinGame(
    val id: PendingGameId,
    val password: String? = null,
)

typealias HostQuit = Unit
