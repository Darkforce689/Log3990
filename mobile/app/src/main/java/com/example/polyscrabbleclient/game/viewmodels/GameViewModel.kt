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

    fun canPassTurn(): Boolean {
        TODO("Not yet implemented")
    }

    fun canPlaceLetter(): Boolean {
        TODO("Not yet implemented")
    }

    fun canExchangeLetter(): Boolean {
        TODO("Not yet implemented")
    }

    fun canCancel(): Boolean {
        TODO("Not yet implemented")
    }

    fun passTurn() {
        TODO("Not yet implemented")
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
