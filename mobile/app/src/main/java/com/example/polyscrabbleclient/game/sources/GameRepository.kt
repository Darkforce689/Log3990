package com.example.polyscrabbleclient.game.sources

import com.example.polyscrabbleclient.game.model.GameModel
import com.example.polyscrabbleclient.lobby.sources.OnlineGameSettings

const val millisecondsInSecond = 1000

object GameRepository {

    fun createPlayers(playerNames: ArrayList<String>): List<Player>{
        return playerNames.map { playerName -> Player(playerName) }
    }

    fun receiveInitialGameSettings(gameSettings: OnlineGameSettings) {
        game.turnTotalTime.value = gameSettings.timePerTurn / millisecondsInSecond
        game.turnRemainingTime.value = gameSettings.timePerTurn / millisecondsInSecond
        game.createPlayersFrom(gameSettings.playerNames)
        game.gameMode.value = gameSettings.gameMode
        game.board.gameMode = game.gameMode.value
        game.players.value = createPlayers(gameSettings.playerNames)
        gameSocket.emit(EmitGameEvent.JoinGame, gameSettings.id)
    }

    private val gameSocket = GameSocketHandler
    val game = GameModel()

    private val onStartTime: (startTime: StartTime?) -> Unit = { startTime ->
        startTime?.let {
            game.turnTotalTime.value = it / millisecondsInSecond
        }
    }

    private val onRemainingTime: (remainingTime: RemainingTime?) -> Unit = { remainingTime ->
        remainingTime?.let {
            game.turnRemainingTime.value = it / millisecondsInSecond
        }
    }

    private val onGameState: (gameState: GameState?) -> Unit = { gameState ->
        gameState?.let {
            game.update(it)
        }
    }

    private val onTransitionGameState: (transitionGameState: TransitionGameState?) -> Unit =
        { transitionGameState ->
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

    fun emitNextAction(onlineAction: OnlineAction) {
        gameSocket.emit(EmitGameEvent.NextAction, onlineAction)
    }

    fun quitGame() {
        gameSocket.closeConnection()
    }
}
