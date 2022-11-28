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
        OnLobbyEvent.LobbyGames,
        LobbyGames::class.java
    ),
    Pair(
        OnLobbyEvent.LobbyGameId,
        LobbyGameId::class.java
    ),
    Pair(
        OnLobbyEvent.ConfirmJoin,
        ConfirmJoin::class.java
    ),
    Pair(
        OnLobbyEvent.HostQuit,
        HostQuit::class.java
    ),
    Pair(
        OnLobbyEvent.PlayerRefused,
        PlayerRefused::class.java
    ),
    Pair(
        OnLobbyEvent.PlayerKicked,
        PlayerKicked::class.java
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
    Pair(
        EmitLobbyEvent.KickPlayer,
        KickPlayer::class.java
    ),
    Pair(
        EmitLobbyEvent.AcceptPlayer,
        AcceptPlayer::class.java
    ),
    Pair(
        EmitLobbyEvent.RefusePlayer,
        RefusePlayer::class.java
    ),
)

class OnLobbyEvent(override val eventName: String) : OnEvent(eventName) {
    companion object {
        val GameJoined = OnLobbyEvent("gameJoined")
        val GameStarted = OnLobbyEvent("gameStarted")
        val LobbyGames = OnLobbyEvent("pendingGames")
        val LobbyGameId = OnLobbyEvent("pendingGameId")
        val ConfirmJoin = OnLobbyEvent("confirmPassword")
        val PlayerRefused = OnLobbyEvent("playerRefused")
        val PlayerKicked = OnLobbyEvent("playerKicked")
        val HostQuit = OnLobbyEvent("hostQuit")
        val Error = OnLobbyEvent("error")
    }
}

class EmitLobbyEvent(override val eventName: String) : EmitEvent(eventName) {
    companion object {
        val CreateGame = EmitLobbyEvent("createGame")
        val LaunchGame = EmitLobbyEvent("launchGame")
        val JoinGame = EmitLobbyEvent("joinGame")
        val KickPlayer = EmitLobbyEvent("kickPlayer")
        val AcceptPlayer = EmitLobbyEvent("acceptPlayer")
        val RefusePlayer = EmitLobbyEvent("refusePlayer")
    }
}
