package com.example.polyscrabbleclient.game.sources

import com.example.polyscrabbleclient.game.model.GameModel
import com.example.polyscrabbleclient.game.viewmodels.TileCoordinates
import com.example.polyscrabbleclient.lobby.sources.OnlineGameSettings
import com.example.polyscrabbleclient.utils.Repository


object GameRepository : Repository<GameModel, GameSocketHandler>() {

    override lateinit var model: GameModel
    override val socket = GameSocketHandler

    private fun createPlayers(playerNames: ArrayList<String>): List<Player> {
        return playerNames.map { playerName -> Player(playerName) }
    }

    fun receiveInitialGameSettings(gameSettings: OnlineGameSettings) {
        model.turnTotalTime.value = gameSettings.timePerTurn
        model.turnRemainingTime.value = gameSettings.timePerTurn
        model.players.addAll(createPlayers(gameSettings.playerNames))
        model.gameMode.value = gameSettings.gameMode
        model.board.gameMode = model.gameMode.value
        model.setupObserver(gameSettings.observerNames)
        socket.emit(EmitGameEvent.JoinGame, gameSettings.id)
        socket.onGameDisconnected(onGameDisconnected)
    }

    private val onStartTime: (startTime: StartTime?) -> Unit = { startTime ->
        startTime?.let {
            model.turnTotalTime.value = it
        }
    }

    private val onRemainingTime: (remainingTime: RemainingTime?) -> Unit = { remainingTime ->
        remainingTime?.let {
            model.turnRemainingTime.value = it
        }
    }

    private val onGameState: (gameState: GameState?) -> Unit = { gameState ->
        gameState?.let {
            model.update(it)
        }
    }

    private val onSyncState: (syncState: SyncState?) -> Unit = { syncState ->
        syncState?.let {
            model.updateSync(it)
        }
    }

    private val onTransitionGameState: (transitionGameState: TransitionGameState?) -> Unit =
        { transitionGameState ->
            transitionGameState?.let {
                model.updatePlayerName(it.previousPlayerName, it.name)
            }
        }

    private val onGameDisconnected: () -> Unit = {
        model.disconnected.value = true
        model.isGameActive.value = false
    }

    fun emitNextAction(onlineAction: OnlineAction) {
        socket.emit(EmitGameEvent.NextAction, onlineAction)
    }

    fun emitNextSync(sync: SyncState) {
        socket.emit(EmitGameEvent.NextSync, sync)
    }

    fun quitGame() {
        reset()
    }

    init {
        setup()
    }

    override fun reset() {
        socket.clearEventsCallbacks()
        super.reset()
    }

    override fun setup() {
        model = GameModel()
        super.setup()
    }

    override fun setupEvents() {
        socket.on(OnGameEvent.StartTime, onStartTime)
        socket.on(OnGameEvent.RemainingTime, onRemainingTime)
        socket.on(OnGameEvent.GameState, onGameState)
        socket.on(OnGameEvent.SyncState, onSyncState)
        socket.on(OnGameEvent.TransitionGameState, onTransitionGameState)
    }

    var lastPosition: ArrayList<Position> = arrayListOf()
    fun sendSync(coordinatesSet: MutableSet<TileCoordinates>) {
        lastPosition = ArrayList(coordinatesSet.map { coordinate ->
            Position(
                coordinate.column - 1,
                coordinate.row - 1
            )
        })

        val sync = SyncState(
            positions = lastPosition,
        )
        emitNextSync(sync)
    }

    fun sendContinuousSync(positions: ArrayList<Position>) {
        positions.addAll(lastPosition)
        emitNextSync(SyncState(positions = positions));
    }

}
