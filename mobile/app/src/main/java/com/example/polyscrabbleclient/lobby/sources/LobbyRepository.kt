package com.example.polyscrabbleclient.lobby.sources

import com.example.polyscrabbleclient.game.sources.GameRepository
import com.example.polyscrabbleclient.invitations.utils.GameInviteBroker
import com.example.polyscrabbleclient.lobby.model.LobbyModel
import com.example.polyscrabbleclient.message.domain.ConversationsManager
import com.example.polyscrabbleclient.utils.Repository
import kotlinx.coroutines.flow.MutableStateFlow

object LobbyRepository : Repository<LobbyModel, LobbySocketHandler>() {

    override var model: LobbyModel = LobbyModel()
    override val socket = LobbySocketHandler

    private val onGameJoined: (gameJoined: GameJoined?) -> Unit = { gameJoined ->
        gameJoined?.let {
            model.currentPendingGameId.value = it.id
            model.pendingGamePlayerNames.value = it.playerNames
            model.playerNamesInLobby.tryEmit(it.playerNames)
            model.password.value = it.password
        }
    }

    private val onGameStarted: (gameStarted: GameStarted?) -> Unit = { gameStarted ->
        gameStarted?.let {
            GameRepository.receiveInitialGameSettings(it)
            val gameToken = it.id
            ConversationsManager.joinGameConversation(gameToken)
            GameInviteBroker.destroyInvite() // TODO Change if join server sends join confirmation
        }
    }

    private val onPendingGames: (lobbyGames: LobbyGames?) -> Unit = { newLobbyGames ->
        newLobbyGames?.let {
            model.pendingGames.value = it.pendingGamesSettings
            model.observableGames.value = it.observableGamesSettings
        }
    }

    private val onPendingGameId: (lobbyGameId: LobbyGameId?) -> Unit = { newLobbyGameId ->
        newLobbyGameId?.let {
            model.currentPendingGameId.value = it
        }
    }

    private val onHostQuit: (hostQuit: HostQuit?) -> Unit = {
        model.hostHasJustQuitTheGame.value = true
    }

    private var onErrorCallbacks: MutableMap<String, (Error?) -> Unit> = HashMap()
    fun subscribeOnError(key: String, callback: (Error?) -> Unit) {
        onErrorCallbacks[key] = callback
    }

    val onError: (error: Error?) -> Unit = { error ->
        onErrorCallbacks.forEach { _, callback ->
            callback(error)
        }
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

    private fun clearCallBacks() {
        onErrorCallbacks.clear()
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
