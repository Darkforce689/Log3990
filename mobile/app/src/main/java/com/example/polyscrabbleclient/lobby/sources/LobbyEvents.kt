package com.example.polyscrabbleclient.lobby.sources

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
        PendingGames::class.java
    ),
    Pair(
        OnLobbyEvent.PendingGameId,
        PendingGameId::class.java
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

enum class OnLobbyEvent (val eventName: String) {
    GameJoined("gameJoined"),
    GameStarted("gameStarted"),
    PendingGames("pendingGames"),
    PendingGameId("pendingGameId"),
    Error("error"),
}

enum class EmitLobbyEvent (val eventName: String) {
    CreateGame("createGame"),
    LaunchGame("launchGame"),
    JoinGame("joinGame"),
}
