package com.example.polyscrabbleclient.lobby.sources

import com.example.polyscrabbleclient.game.sources.GameRepository
import com.example.polyscrabbleclient.lobby.model.LobbyModel
import com.example.polyscrabbleclient.message.domain.ConversationsManager
import com.example.polyscrabbleclient.utils.Repository

object LobbyRepository : Repository<LobbyModel, LobbySocketHandler>()  {

    override var model: LobbyModel = LobbyModel()
    override val socket = LobbySocketHandler

    private val onGameJoined: (gameJoined: GameJoined?) -> Unit = { gameJoined ->
        gameJoined?.let {
            model.currentPendingGameId.value = it.id
            model.pendingGamePlayerNames.value = it.playerNames
        }
    }

    private val onGameStarted: (gameStarted: GameStarted?) -> Unit = { gameStarted ->
        gameStarted?.let {
            GameRepository.receiveInitialGameSettings(it)
            val gameToken = it.id
            ConversationsManager.joinGameConversation(gameToken)
        }
    }

    private val onPendingGames: (pendingGames: PendingAndObservableGames?) -> Unit = { newPendingGames ->
        newPendingGames?.let {
            model.pendingGames.value = it.pendingGamesSettings
            model.observableGames.value = it.observableGamesSettings
        }
    }

    private val onPendingGameId: (pendingGameId: PendingGameId?) -> Unit = { pendingGameId ->
        pendingGameId?.let {
            model.currentPendingGameId.value = it
        }
    }

    private val onHostQuit: (hostQuit: HostQuit?) -> Unit = {
        model.hostHasJustQuitTheGame.value = true
    }

    private val onError: (error: Error?) -> Unit = { error ->
        println("LobbyRepository -> Error : $error")
    }

    fun emitJoinGame(joinGame: JoinGame, navigateToGameScreen: () -> Unit) {
        socket.on(OnLobbyEvent.GameStarted) { _: GameStarted? ->
            navigateToGameScreen()
        }
        socket.emit(EmitLobbyEvent.JoinGame, joinGame)
    }

    fun emitCreateGame(newGameParam: CreateGame) {
        model.isPendingGameHost.value = true
        socket.emit(EmitLobbyEvent.CreateGame, newGameParam)
    }

    fun emitLaunchGame(navigateToGameScreen: () -> Unit) {
        model.currentPendingGameId.value.let {
            socket.on(OnLobbyEvent.GameStarted) { _: GameStarted? ->
                reset()
                navigateToGameScreen()
            }
            socket.emit(EmitLobbyEvent.LaunchGame, it)
        }
    }

    fun quitPendingGame() {
        reset()
    }

    init {
        setup()
    }

    override fun setup() {
        model = LobbyModel()
        super.setup()
    }

    override fun setupEvents() {
        socket.on(OnLobbyEvent.GameJoined, onGameJoined)
        socket.on(OnLobbyEvent.GameStarted, onGameStarted)
        socket.on(OnLobbyEvent.PendingGames, onPendingGames)
        socket.on(OnLobbyEvent.PendingGameId, onPendingGameId)
        socket.on(OnLobbyEvent.HostQuit, onHostQuit)
        socket.on(OnLobbyEvent.Error, onError)
    }
}
