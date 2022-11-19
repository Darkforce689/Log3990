package com.example.polyscrabbleclient.account.model

import com.example.polyscrabbleclient.game.sources.GameState
import com.example.polyscrabbleclient.lobby.sources.GameMode

data class GameHistory(val games: ArrayList<GameHistoryInfo>, val userId: String)

data class GameHistoryInfo(
    val gameToken: String,
    val gameMode: GameMode,
    val userIds: ArrayList<String>,
    val winnerIds: ArrayList<String>?,
    val date: Double,
    val forfeitedIds: ArrayList<String>?
)

data class GameStateHistory(val gameState: GameState, val gameToken: String, val date: Double)
