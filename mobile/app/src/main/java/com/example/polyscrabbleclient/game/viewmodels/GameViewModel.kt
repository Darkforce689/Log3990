package com.example.polyscrabbleclient.game.viewmodels

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.game.sources.*

class GameViewModel: ViewModel() {
    val game = GameRepository.game
    var remainingLettersCount = game.remainingLettersCount
    var turnRemainingTime = game.turnRemainingTime
    var turnTotalTime = game.turnTotalTime

    fun getRemainingTimeFraction(
        current: Int = turnRemainingTime.value,
        total: Int = turnTotalTime.value
    ): Float {
        return current.toFloat() / total
    }

    private fun isActivePlayer(): Boolean {
        return GameRepository.game.getActivePlayer() === GameRepository.game.user.value
    }

    fun canPassTurn(): Boolean {
        return isActivePlayer()
    }

    fun canPlaceLetter(): Boolean {
        // TODO
        return false
    }

    fun canExchangeLetter(): Boolean {
        // TODO
        return false
    }

    fun canCancel(): Boolean {
        // TODO
        return false
    }

    fun passTurn() {
        GameRepository.emitNextAction(OnlineActionType.Pass)
    }

    fun placeLetter() {
        TODO("Not yet implemented")
    }

    fun exchangeLetter() {
        TODO("Not yet implemented")
    }

    fun cancel() {
        TODO("Not yet implemented")
    }
}
