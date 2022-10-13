package com.example.polyscrabbleclient.game.sources

val GameEventTypes = mapOf(
    Pair(
        OnGameEvent.GameState,
        GameState::class.java
    ),
    Pair(
        OnGameEvent.StartTime,
        StartTime::class.java
    ),
    Pair(
        OnGameEvent.RemainingTime,
        RemainingTime::class.java
    ),
    Pair(
        OnGameEvent.TransitionGameState,
        TransitionGameState::class.java
    ),
    Pair(
        EmitGameEvent.JoinGame,
        JoinGame::class.java
    ),
    Pair(
        EmitGameEvent.NextAction,
        NextAction::class.java
    ),
    Pair(
        EmitGameEvent.Disconnect,
        Disconnect::class.java
    ),
)

enum class OnGameEvent (val eventName: String) {
    GameState("gameState"),
    StartTime("timerStartingTime"),
    RemainingTime("timeUpdate"),
    TransitionGameState("transitionGameState"),
}

enum class EmitGameEvent (val eventName: String) {
    JoinGame("joinGame"),
    NextAction("nextAction"),
    Disconnect("disconnect"),
}
