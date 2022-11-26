package com.example.polyscrabbleclient.lobby.sources

import androidx.navigation.NavController
import com.example.polyscrabbleclient.NavPage
import com.example.polyscrabbleclient.game.sources.GameRepository
import com.example.polyscrabbleclient.invitations.utils.GameInviteBroker
import com.example.polyscrabbleclient.lobby.model.LobbyModel
import com.example.polyscrabbleclient.message.domain.ConversationsManager
import com.example.polyscrabbleclient.navigateTo
import com.example.polyscrabbleclient.user.User
import com.example.polyscrabbleclient.utils.Repository

object LobbyRepository : Repository<LobbyModel, LobbySocketHandler>() {

    override var model: LobbyModel = LobbyModel()
    override val socket = LobbySocketHandler

    private val onGameJoined: (gameJoined: GameJoined?) -> Unit = { gameJoined ->
        gameJoined?.let {
            model.currentPendingGameId.value = it.id
            model.pendingGamePlayerNames.value = it.playerNames
            model.isGamePrivate.value = it.privateGame
            model.isGameProtected.value = it.password?.isNotEmpty() ?: false
            model.candidatePlayerNames.value = it.tmpPlayerNames
            model.isAcceptedPlayer.value = it.playerNames.contains(User.name)
            model.playerNamesInLobby.tryEmit(it.playerNames)
            model.password.value = it.password
            val gameToken = it.id
            ConversationsManager.joinGameConversation(gameToken)
        }
    }

    private val onGameStarted: (GameStarted?) -> Unit = { gameStarted ->
        gameStarted?.let {
            GameRepository.receiveInitialGameSettings(it)
            GameInviteBroker.destroyInvite() // TODO Change if join server sends join confirmation
        }
    }

    private val onPendingGames: (LobbyGames?) -> Unit = { newLobbyGames ->
        newLobbyGames?.let {
            model.pendingGames.value = it.pendingGamesSettings
            model.observableGames.value = it.observableGamesSettings
        }
    }

    private val onPendingGameId: (LobbyGameId?) -> Unit = { newLobbyGameId ->
        newLobbyGameId?.let {
            model.currentPendingGameId.value = it
        }
    }

    private val onConfirmJoin: (ConfirmJoin?) -> Unit = { newConfirmJoin ->
        newConfirmJoin?.let {
            model.hasJustConfirmedJoin.value = it
        }
    }

    private val onHostQuit: (HostQuit?) -> Unit = {
        model.hostHasJustQuitTheGame.value = true
    }

    private var onErrorCallbacks: MutableMap<String, (Error?) -> Unit> = HashMap()
    fun subscribeOnError(key: String, callback: (Error?) -> Unit) {
        onErrorCallbacks[key] = callback
    }

    private val onError: (error: Error?) -> Unit = { error ->
        onErrorCallbacks.forEach { (_, callback) ->
            callback(error)
        }
        println("LobbyRepository -> Error : $error")
    }

    fun emitJoinGame(
        joinGame: JoinGame,
        navController: NavController
    ) {
        socket.on(OnLobbyEvent.GameStarted) { _: GameStarted? ->
            navigateTo(NavPage.GamePage, navController)
        }
        socket.on(OnLobbyEvent.ConfirmJoin) { confirmJoin: ConfirmJoin? ->
            confirmJoin?.let {
                if (it) {
                    navigateTo(NavPage.WaitingRoom, navController)
                }
            }
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

    fun acceptPlayer(playerName: String) {
        model.currentPendingGameId.value?.let {
            val acceptPlayerEvent = AcceptPlayer(it, playerName)
            socket.emit(EmitLobbyEvent.AcceptPlayer, acceptPlayerEvent)
        }
    }

    fun refusePlayer(playerName: String) {
        model.currentPendingGameId.value?.let {
            val refusePlayerEvent = RefusePlayer(it, playerName)
            socket.emit(EmitLobbyEvent.RefusePlayer, refusePlayerEvent)
        }
    }

    fun kickPlayer(playerName: String) {
        model.currentPendingGameId.value?.let {
            val kickPlayerEvent = KickPlayer(it, playerName)
            socket.emit(EmitLobbyEvent.KickPlayer, kickPlayerEvent)
        }
    }

    fun leaveLobbyGame() {
        reset()
    }

    // NOT CALLED ?
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
        socket.on(OnLobbyEvent.LobbyGames, onPendingGames)
        socket.on(OnLobbyEvent.LobbyGameId, onPendingGameId)
        socket.on(OnLobbyEvent.ConfirmJoin, onConfirmJoin)
        socket.on(OnLobbyEvent.HostQuit, onHostQuit)
        socket.on(OnLobbyEvent.Error, onError)
    }
}
