package com.example.polyscrabbleclient.lobby.sources

import com.example.polyscrabbleclient.game.sources.IMagicCard
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
    val password: String? = null,
    val drawableMagicCards: ArrayList<IMagicCard>,
    val numberOfBots: Int? = null,
    val observerNames: ArrayList<String>? = null,
) {
    fun isProtected(): Boolean {
        return password?.isNotEmpty() ?: false
    }
}

typealias LobbyGamesList = ArrayList<OnlineGameSettings>

data class PrivateGameEvent(
    val gameId: String,
    val playerName: String
)

// Warning : Events Data Classes have to match the backend corresponding interfaces

typealias GameJoined = OnlineGameSettings

typealias GameStarted = OnlineGameSettings

data class LobbyGames(
    val pendingGamesSettings: LobbyGamesList,
    val observableGamesSettings: LobbyGamesList
)

typealias LobbyGameId = String

typealias ConfirmJoin = Boolean

typealias HostQuit = Unit

typealias Error = String

data class CreateGame(
    val gameMode: GameMode,
    val timePerTurn: Int,
    val playerNames: ArrayList<String>,
    val tmpPlayerNames: ArrayList<String>,
    val privateGame: Boolean,
    val password: String? = null,
    val randomBonus: Boolean,
    val botDifficulty: BotDifficulty,
    val numberOfPlayers: Int,
    val magicCardIds: ArrayList<String>?,
)

typealias LaunchGame = LobbyGameId

data class JoinGame(
    val id: LobbyGameId,
    val password: String? = null,
)

typealias KickPlayer = PrivateGameEvent

typealias AcceptPlayer = PrivateGameEvent

typealias RefusePlayer = PrivateGameEvent


