package com.example.polyscrabbleclient.game.viewmodels

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.game.domain.BoardCrawler
import com.example.polyscrabbleclient.game.model.TileModel
import com.example.polyscrabbleclient.game.sources.*
import com.example.polyscrabbleclient.ui.theme.leaveButtonFR
import com.example.polyscrabbleclient.ui.theme.quitButtonFR
import com.example.polyscrabbleclient.lobby.sources.GameMode

class GameViewModel : ViewModel() {
    val game = GameRepository.game
    var remainingLettersCount = game.remainingLettersCount
    var turnRemainingTime = game.turnRemainingTime
    var turnTotalTime = game.turnTotalTime
    var gameMode = game.gameMode

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

    private fun isExactlyOneLetterSelected(): Boolean {
        return getLetterRackTiles().count { letter -> letter.isSelected.value } == 1
    }

    private fun isAtLeastOneLetterUsedOnBoard(): Boolean {
        return getLetterRackTiles().count { letter -> letter.isUsedOnBoard.value } > 0
    }

    private fun hasSelectedPosition(): Boolean {
        return getSelectedCoordinates() != null
    }

    private fun hasNoLetterOnSelected(): Boolean {
        var tileCoordinates = getSelectedCoordinates()
        return game.board.tileGrid[tileCoordinates!!.row - 1][tileCoordinates!!.column - 1].content.value == null
    }

    private fun hasNoBonusOnSelected(): Boolean {
        var tileCoordinates = getSelectedCoordinates()
        val tileModel =
            game.board.tileGrid[tileCoordinates!!.row - 1][tileCoordinates!!.column - 1]
        return tileModel!!.letterMultiplier == 1 && tileModel!!.wordMultiplier == 1
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

    private fun getLetterRackTiles(): MutableList<TileModel> {
        return game.userLetters
    }

    private fun getSelectedCoordinates(): TileCoordinates?{
        return game.board.selectedCoordinates.value
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

    fun getQuitLabel(): String {
        return if (game.hasGameEnded()) {
            leaveButtonFR
        } else {
            quitButtonFR
        }
    }

    fun isMagicGame(): Boolean {
        return gameMode.value == GameMode.Magic
    }

    fun canUseMagicCards(): Boolean {
        return isActivePlayer() && isMagicGame();
    }

    fun canExchangeMagicCard(): Boolean {
        return canUseMagicCards() && isExactlyOneLetterSelected()
    }

    fun canPlaceRandomBonusMagicCard(): Boolean {
        return canUseMagicCards() && hasSelectedPosition() && hasNoLetterOnSelected() && hasNoBonusOnSelected()
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

    fun quitGame() {
        GameRepository.quitGame()
    }

    fun splitPoints() {
        GameRepository.emitNextAction(OnlineAction(OnlineActionType.SplitPoints))
    }

    fun exchangeHorse() {
        GameRepository.emitNextAction(OnlineAction(OnlineActionType.ExchangeHorse))
    }

    fun exchangeHorseAll() {
        GameRepository.emitNextAction(OnlineAction(OnlineActionType.ExchangeHorseAll))
    }

    fun skipNextTurn() {
        GameRepository.emitNextAction(OnlineAction(OnlineActionType.SkipNextTurn))
    }

    fun extraTurn() {
        GameRepository.emitNextAction(OnlineAction(OnlineActionType.ExtraTurn))
    }

    fun reduceTimer() {
        GameRepository.emitNextAction(OnlineAction(OnlineActionType.ReduceTimer))
    }

    fun exchangeALetter() {
        val letters = getSelectedTiles()
            .map { tileModel -> tileModel.letter.toString() }
            .reduce { lettersString, letter -> lettersString + letter }
        GameRepository.emitNextAction(
            OnlineAction(
                OnlineActionType.ExchangeALetter,
                letters = letters
            )
        )
    }

    fun placeRandomBonus() {
        val position =
            Position(getSelectedCoordinates()!!.column - 1, getSelectedCoordinates()!!.row - 1)
        game.board.unselect()
        GameRepository.emitNextAction(
            OnlineAction(
                OnlineActionType.PlaceBonus,
                position = position
            )
        )
    }
}
