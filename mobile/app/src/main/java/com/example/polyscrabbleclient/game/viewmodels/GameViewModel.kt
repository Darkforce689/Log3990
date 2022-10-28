package com.example.polyscrabbleclient.game.viewmodels

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.game.domain.BoardCrawler
import com.example.polyscrabbleclient.game.model.TileModel
import com.example.polyscrabbleclient.game.sources.*

class GameViewModel : ViewModel() {
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
        return game.getActivePlayer() === game.getUser()
    }

    private fun isAtLeastOneLetterSelected(): Boolean {
        return getLetterRackTiles().count { letter -> letter.isSelected.value } > 0
    }

    private fun isAtLeastOneLetterUsedOnBoard(): Boolean {
        return getLetterRackTiles().count { letter -> letter.isUsedOnBoard.value } > 0
    }

    private fun restoreBoard() {
        game.board.removeTransientTilesContent()
    }

    private fun restoreLetterRack() {
        getLetterRackTiles().forEach { tile ->
            tile.isUsedOnBoard.value = false
            tile.isSelected.value = false
        }
    }

    private fun getLetterRackTiles() : MutableList<TileModel> {
        return game.userLetters
    }

    private fun getSelectedTiles(): List<TileModel> {
        return getLetterRackTiles().filter { letter -> letter.isSelected.value }
    }

    fun canPassTurn(): Boolean {
        return isActivePlayer()
    }

    fun canPlaceLetter(): Boolean {
        return isActivePlayer() && isAtLeastOneLetterUsedOnBoard()
    }

    fun canExchangeLetter(): Boolean {
        return isActivePlayer() && isAtLeastOneLetterSelected()
    }

    fun canCancel(): Boolean {
        return canExchangeLetter() || canPlaceLetter()
    }

    fun passTurn() {
        GameRepository.emitNextAction(OnlineAction(OnlineActionType.Pass))
    }

    fun placeLetter() {
        val letterRack = ArrayList(
            getLetterRackTiles().map { tileModel ->
                Letter(tileModel.letter.toString(), tileModel.points)
            }
        )
        val placement = BoardCrawler.getPlacement()
        if (placement === null) {
            cancel()
            return
        }
        val (placementSetting, word) = placement
        GameRepository.emitNextAction(
            OnlineAction(
                OnlineActionType.Place,
                letters = word,
                letterRack = letterRack,
                placementSettings = placementSetting.adjustPlacementSetting()
            )
        )
    }

    fun exchangeLetter() {
        val letters = getSelectedTiles()
            .map { tileModel -> tileModel.letter.toString() }
            .reduce { lettersString, letter -> lettersString + letter }
        GameRepository.emitNextAction(OnlineAction(OnlineActionType.Exchange, letters = letters))
    }

    fun cancel() {
        restoreLetterRack()
        restoreBoard()
    }
}
