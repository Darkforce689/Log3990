package com.example.polyscrabbleclient.lobby.sources

object LobbyRepository {
    private val lobbySocket = LobbySocketHandler

    private val onGameJoined: (gameJoined: GameJoined?) -> Unit = { gameJoined ->
        TODO()
    }

    private val onGameStarted: (gameStarted: GameStarted?) -> Unit = { gameStarted ->
        TODO()
    }

    private val onPendingGames: (pendingGames: PendingGames?) -> Unit = { pendingGames ->
        TODO()
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
    }
}
