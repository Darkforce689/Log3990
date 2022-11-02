package com.example.polyscrabbleclient.lobby.sources

import androidx.compose.runtime.mutableStateOf
import com.example.polyscrabbleclient.game.sources.GameRepository

object LobbyRepository {

    private val lobbySocket = LobbySocketHandler
    val pendingGames = mutableStateOf<PendingGames?>(null)
    val observableGames = mutableStateOf<ObservableGames?>(null)
    val currentPendingGameId = mutableStateOf<PendingGameId?>(null)
    val pendingGamePlayerNames = mutableStateOf(listOf<String>())
    val isPendingGameHost = mutableStateOf(false)

    val hostHasJustQuitTheGame = mutableStateOf(false)

    private val onGameJoined: (gameJoined: GameJoined?) -> Unit = { gameJoined ->
        gameJoined?.let {
            currentPendingGameId.value = it.id
            pendingGamePlayerNames.value = it.playerNames
        }
    }

    private val onGameStarted: (gameStarted: GameStarted?) -> Unit = { gameStarted ->
        gameStarted?.let {
            GameRepository.receiveInitialGameSettings(it)
        }
    }

    private val onPendingGames: (pendingGames: PendingAndObservableGames?) -> Unit = { newPendingGames ->
        newPendingGames?.let {
            pendingGames.value = it.pendingGamesSettings
            observableGames.value = it.observableGamesSettings
        }
    }

    private val onPendingGameId: (pendingGameId: PendingGameId?) -> Unit = { pendingGameId ->
        pendingGameId?.let {
            currentPendingGameId.value = it
        }
    }

    private val onHostQuit: (hostQuit: HostQuit?) -> Unit = {
        hostHasJustQuitTheGame.value = true
    }

    private val onError: (error: Error?) -> Unit = { error ->
        println("LobbyRepository -> Error : $error")
    }

    fun emitJoinGame(joinGame: JoinGame, navigateToGameScreen: () -> Unit) {
        lobbySocket.on(OnLobbyEvent.GameStarted) { _: GameStarted? ->
            navigateToGameScreen()
        }
        lobbySocket.emit(EmitLobbyEvent.JoinGame, joinGame)
    }

    init {
        setupSocket()
    }

    private fun setupSocket() {
        lobbySocket.setSocket()
        lobbySocket.on(OnLobbyEvent.GameJoined, onGameJoined)
        lobbySocket.on(OnLobbyEvent.GameStarted, onGameStarted)
        lobbySocket.on(OnLobbyEvent.PendingGames, onPendingGames)
        lobbySocket.on(OnLobbyEvent.PendingGameId, onPendingGameId)
        lobbySocket.on(OnLobbyEvent.HostQuit, onHostQuit)
        lobbySocket.on(OnLobbyEvent.Error, onError)
        lobbySocket.ensureConnection()
    }

    fun emitCreateGame(newGameParam: CreateGame) {
        isPendingGameHost.value = true
        lobbySocket.emit(EmitLobbyEvent.CreateGame, newGameParam)
    }

    fun emitLaunchGame(navigateToGameScreen: () -> Unit) {
        currentPendingGameId.value.let {
            lobbySocket.on(OnLobbyEvent.GameStarted) { _: GameStarted? ->
                resetPendingGame()
                navigateToGameScreen()
            }
            lobbySocket.emit(EmitLobbyEvent.LaunchGame, it)
        }
    }

    fun quitPendingGame() {
        lobbySocket.disconnect()
        resetPendingGame()
        setupSocket()
    }

    private fun resetPendingGame() {
        currentPendingGameId.value = null
        isPendingGameHost.value = false
        pendingGamePlayerNames.value = listOf()
        hostHasJustQuitTheGame.value = false
    }
}
