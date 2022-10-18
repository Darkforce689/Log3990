package com.example.polyscrabbleclient.game.sources

import com.example.polyscrabbleclient.game.model.GameModel
import com.example.polyscrabbleclient.lobby.sources.OnlineGameSettings

object GameRepository {

    fun receiveInitialGameSettings(gameSettings: OnlineGameSettings) {
        // TODO : UPDATE (ONLY TEMPORARY WHILE WAITING FOR !54)
        gameSocket.emit(EmitGameEvent.JoinGame, UserAuth("helloFrom2015", gameSettings.id))
    }

    private val gameSocket = GameSocketHandler
    val game = GameModel()

    private val onStartTime: (startTime: StartTime?) -> Unit = { startTime ->
        startTime?.let {
            game.turnRemainingTime.value = it
        }
    }

    private val onRemainingTime: (remainingTime: RemainingTime?) -> Unit = { remainingTime ->
        remainingTime?.let {
            game.turnTotalTime.value = it
        }
    }

    private val onGameState: (gameState: GameState?) -> Unit = { gameState ->
        gameState?.let {
            game.update(it)
        }
    }

    private val onTransitionGameState: (transitionGameState: TransitionGameState?) -> Unit = { transitionGameState ->
        // TODO
        println("onTransitionGameState $transitionGameState")
    }

    init {
        gameSocket.setSocket()
        gameSocket.ensureConnection()
        gameSocket.on(OnGameEvent.StartTime, onStartTime)
        gameSocket.on(OnGameEvent.RemainingTime, onRemainingTime)
        gameSocket.on(OnGameEvent.GameState, onGameState)
        gameSocket.on(OnGameEvent.TransitionGameState, onTransitionGameState)
    }
}
