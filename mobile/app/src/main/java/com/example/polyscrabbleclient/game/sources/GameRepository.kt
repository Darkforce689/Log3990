package com.example.polyscrabbleclient.game.sources

import com.example.polyscrabbleclient.game.model.GameModel

object GameRepository {
    private val gameSocket = GameSocketHandler
    val game = GameModel()

    private val onStartTime: (startTime: StartTime?) -> Unit = { newStartTime ->
        TODO()
    }

    private val onRemainingTime: (remainingTime: RemainingTime?) -> Unit = { newRemainingTime ->
        TODO()
    }

    private val onGameState: (gameState: GameState?) -> Unit = { newGameState ->
        if (newGameState !== null) {
            game.update(newGameState)
            println(newGameState.players.size)
            println(newGameState.players[0].name)
            println(newGameState.players[0].letterRack.size)
            println(newGameState.grid[0][0].letterObject.char)
        } else {
            println("received null GameState value")
        }
    }

    private val onTransitionGameState: (transitionGameState: TransitionGameState?) -> Unit = { transitionGameState ->
        TODO()
    }

    init {
        gameSocket.setSocket()
        gameSocket.ensureConnection()
        gameSocket.on(OnGameEvent.StartTime, onStartTime)
        gameSocket.on(OnGameEvent.RemainingTime, onRemainingTime)
        gameSocket.on(OnGameEvent.GameState, onGameState)
        gameSocket.on(OnGameEvent.TransitionGameState, onTransitionGameState)
        GameSocketHandler.emit(EmitGameEvent.JoinGame, JoinGame(1))
    }
}
