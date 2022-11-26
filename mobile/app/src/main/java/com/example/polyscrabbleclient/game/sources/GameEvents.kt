package com.example.polyscrabbleclient.game.sources

import com.example.polyscrabbleclient.utils.EmitEvent
import com.example.polyscrabbleclient.utils.OnEvent

val GameEventTypes = mapOf(
    Pair(
        OnGameEvent.GameState,
        GameState::class.java
    ),Pair(
        OnGameEvent.SyncState,
        SyncState::class.java
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
        EmitGameEvent.NextSync,
        NextSync::class.java
    ),
    Pair(
        EmitGameEvent.Disconnect,
        Disconnect::class.java
    ),
)

class OnGameEvent(override val eventName: String) : OnEvent(eventName) {
    companion object {
        val GameState = OnGameEvent("gameState")
        val SyncState = OnGameEvent("syncState")
        val StartTime = OnGameEvent("timerStartingTime")
        val RemainingTime = OnGameEvent("timeUpdate")
        val TransitionGameState = OnGameEvent("transitionGameState")
    }
}

class EmitGameEvent(override val eventName: String) : EmitEvent(eventName) {
    companion object {
        val JoinGame = EmitGameEvent("joinGame")
        val NextAction = EmitGameEvent("nextAction")
        val NextSync = EmitGameEvent("nextSync")
        val Disconnect = EmitGameEvent("disconnect")
    }
}
