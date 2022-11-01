package com.example.polyscrabbleclient.lobby.sources

import com.example.polyscrabbleclient.utils.EmitEvent
import com.example.polyscrabbleclient.utils.OnEvent

val LobbyEventTypes = mapOf(
    Pair(
        OnLobbyEvent.GameJoined,
        GameJoined::class.java
    ),
    Pair(
        OnLobbyEvent.GameStarted,
        GameStarted::class.java
    ),
    Pair(
        OnLobbyEvent.PendingGames,
        PendingAndObservableGames::class.java
    ),
    Pair(
        OnLobbyEvent.PendingGameId,
        PendingGameId::class.java
    ),
    Pair(
        OnLobbyEvent.HostQuit,
        HostQuit::class.java
    ),
    Pair(
        OnLobbyEvent.Error,
        Error::class.java
    ),
    Pair(
        EmitLobbyEvent.CreateGame,
        CreateGame::class.java
    ),
    Pair(
        EmitLobbyEvent.LaunchGame,
        LaunchGame::class.java
    ),
    Pair(
        EmitLobbyEvent.JoinGame,
        JoinGame::class.java
    ),
)

class OnLobbyEvent(override val eventName: String) : OnEvent(eventName) {
    companion object {
        val GameJoined = OnLobbyEvent("gameJoined")
        val GameStarted = OnLobbyEvent("gameStarted")
        val PendingGames = OnLobbyEvent("pendingGames")
        val PendingGameId = OnLobbyEvent("pendingGameId")
        val HostQuit = OnLobbyEvent("hostQuit")
        val Error = OnLobbyEvent("error")
    }
}

class EmitLobbyEvent(override val eventName: String) : EmitEvent(eventName) {
    companion object {
        val CreateGame = EmitLobbyEvent("createGame")
        val LaunchGame = EmitLobbyEvent("launchGame")
        val JoinGame = EmitLobbyEvent("joinGame")
    }
}
