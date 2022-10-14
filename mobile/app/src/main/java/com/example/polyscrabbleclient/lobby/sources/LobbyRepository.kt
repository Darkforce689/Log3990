package com.example.polyscrabbleclient.lobby.sources

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

object LobbyRepository {
    private val lobbySocket = LobbySocketHandler
    val pendingGames = mutableStateOf<PendingGames?>(null)

    private val onGameJoined: (gameJoined: GameJoined?) -> Unit = { gameJoined ->
        TODO()
    }

    private val onGameStarted: (gameStarted: GameStarted?) -> Unit = { gameStarted ->
        TODO()
    }

    private val onPendingGames: (pendingGames: PendingGames?) -> Unit = { newPendingGames ->
        newPendingGames?.let {
            pendingGames.value = newPendingGames
        }
    }

    private val onPendingGameId: (pendingGameId: PendingGameId?) -> Unit = { pendingGameId ->
        TODO()
    }

    init {
        lobbySocket.setSocket()
        lobbySocket.ensureConnection()
        lobbySocket.on(OnLobbyEvent.GameJoined, onGameJoined)
        lobbySocket.on(OnLobbyEvent.GameStarted, onGameStarted)
        lobbySocket.on(OnLobbyEvent.PendingGames, onPendingGames)
        lobbySocket.on(OnLobbyEvent.PendingGameId, onPendingGameId)
//        lobbySocket.emit(
//            EmitLobbyEvent.JoinGame,
//            JoinGame(
//                id = "0",
//                name = "helloFrom2015"
//            )
//        )
    }
}
