package com.example.polyscrabbleclient.lobby.sources

import androidx.compose.runtime.mutableStateOf
import com.example.polyscrabbleclient.game.sources.GameRepository

object LobbyRepository {

    private val lobbySocket = LobbySocketHandler
    val pendingGames = mutableStateOf<PendingGames?>(null)
    private var isGameOwner = false

    private val onGameJoined: (gameJoined: GameJoined?) -> Unit = { gameJoined ->
        // TODO
        println("onGameJoined $gameJoined")
    }

    private val onGameStarted: (gameStarted: GameStarted?) -> Unit = { gameStarted ->
        // TODO : REMOVE NEXT LINE
        println("onGameStarted $gameStarted")
        gameStarted?.let {
            GameRepository.receiveInitialGameSettings(it)
        }
    }

    private val onPendingGames: (pendingGames: PendingGames?) -> Unit = { newPendingGames ->
        newPendingGames?.let {
            pendingGames.value = newPendingGames
        }
    }

    private val onPendingGameId: (pendingGameId: PendingGameId?) -> Unit = { pendingGameId ->
        // TODO
        println("onPendingGameId $pendingGameId")
    }

    private val onError: (error: Error?) -> Unit = { error ->
        println("LobbyRepository -> Error : $error")
    }

    fun emitJoinGame(gameToken: JoinGame, navigateToGameScreen: () -> Unit) {
        lobbySocket.emit(EmitLobbyEvent.JoinGame, gameToken)
        // TODO : IS THIS ACCEPTABLE ?
        lobbySocket.on(OnLobbyEvent.GameStarted) { _: GameStarted? ->
            navigateToGameScreen()
        }
    }

    init {
        lobbySocket.setSocket()
        lobbySocket.ensureConnection()
        lobbySocket.on(OnLobbyEvent.GameJoined, onGameJoined)
        lobbySocket.on(OnLobbyEvent.GameStarted, onGameStarted)
        lobbySocket.on(OnLobbyEvent.PendingGames, onPendingGames)
        lobbySocket.on(OnLobbyEvent.PendingGameId, onPendingGameId)
        lobbySocket.on(OnLobbyEvent.Error, onError)
    }

    fun emitCreateGame(newGameParam : CreateGame) {
        lobbySocket.emit(EmitLobbyEvent.CreateGame, newGameParam)
        isGameOwner = true
    }
}
