package com.example.polyscrabbleclient.game.sources

import androidx.compose.runtime.mutableStateOf


object GameRepository {
    private val gameSocket = GameSocketHandler

    val startTime = mutableStateOf<GameState?>(null)
    val remainingTime = mutableStateOf<RemainingTime?>(null)
    val gameState = mutableStateOf<GameState?>(null)
    val transitionGameState = mutableStateOf<TransitionGameState?>(null)

    private val onStartTime: (startTime: StartTime?) -> Unit = { newStartTime ->

    }

    private val onRemainingTime: (remainingTime: RemainingTime?) -> Unit = { newRemainingTime ->

    }

    private val onGameState: (gameState: GameState?) -> Unit = { newGameState ->
        if (newGameState !== null) {
            GameRepository.gameState.value = newGameState
            if (GameRepository.gameState.value !== null) {
                println(GameRepository.gameState.value!!.players[0].letterRack[0].char)
                println(GameRepository.gameState.value!!.grid[0][0].letterObject.char)
            } else {
                println("gameState.value is null")
            }
        } else {
            println("received null")
        }
    }

    private val onTransitionGameState: (transitionGameState: TransitionGameState?) -> Unit = { transitionGameState ->

    }

    init {
        GameSocketHandler.setSocket()
        GameSocketHandler.ensureConnection()
        GameSocketHandler.on(OnGameEvent.StartTime, onStartTime)
        GameSocketHandler.on(OnGameEvent.RemainingTime, onRemainingTime)
        GameSocketHandler.on(OnGameEvent.GameState, onGameState)
            }
        }
    }
}
