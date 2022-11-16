package com.example.polyscrabbleclient.game.sources

import com.example.polyscrabbleclient.game.model.GameModel
import com.example.polyscrabbleclient.game.model.millisecondsInSecond
import com.example.polyscrabbleclient.lobby.sources.OnlineGameSettings
import com.example.polyscrabbleclient.utils.Repository


object GameRepository : Repository<GameModel, GameSocketHandler>() {

    override lateinit var model: GameModel
    override val socket = GameSocketHandler

    private fun createPlayers(playerNames: ArrayList<String>): List<Player> {
        return playerNames.map { playerName -> Player(playerName) }
    }

    fun receiveInitialGameSettings(gameSettings: OnlineGameSettings) {
        model.turnTotalTime.value = gameSettings.timePerTurn / millisecondsInSecond
        model.turnRemainingTime.value = gameSettings.timePerTurn / millisecondsInSecond
        model.players.addAll(createPlayers(gameSettings.playerNames))
        model.gameMode.value = gameSettings.gameMode
        model.board.gameMode = model.gameMode.value
        model.setupObserver(gameSettings.observerNames)
        socket.emit(EmitGameEvent.JoinGame, gameSettings.id)
        socket.onGameDisconnected(onGameDisconnected)
    }

    private val onStartTime: (startTime: StartTime?) -> Unit = { startTime ->
        startTime?.let {
            model.turnTotalTime.value = it / millisecondsInSecond
        }
    }

    private val onRemainingTime: (remainingTime: RemainingTime?) -> Unit = { remainingTime ->
        remainingTime?.let {
            model.turnRemainingTime.value = it / millisecondsInSecond
        }
    }

    private val onGameState: (gameState: GameState?) -> Unit = { gameState ->
        gameState?.let {
            model.update(it)
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
        socket.on(OnGameEvent.TransitionGameState, onTransitionGameState)
    }
}
