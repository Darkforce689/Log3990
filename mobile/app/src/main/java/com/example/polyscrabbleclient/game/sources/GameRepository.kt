package com.example.polyscrabbleclient.game.sources

import androidx.compose.runtime.mutableStateOf
import com.example.polyscrabbleclient.game.model.GameModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


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
            game.gameState.value = newGameState
            // TODO : REMOVE BLOCK
            if (game.gameState.value !== null) {
                println(game.gameState.value!!.players[0].letterRack[0].char)
                println(game.gameState.value!!.grid[0][0].letterObject.char)
            } else {
                println("gameState.value is null")
            }
        } else {
            // TODO : THROW ERROR
            println("received null")
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
        runBlocking {
            launch {
                delay(3000L)
                println("emit EmitGameEvent.JoinGame")
                GameSocketHandler.emit(EmitGameEvent.JoinGame, JoinGame(1))
            }
        }
    }
}
