package com.example.polyscrabbleclient.game.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.polyscrabbleclient.game.domain.JokerChar
import com.example.polyscrabbleclient.game.viewmodels.TileCoordinates

class JokerModel(private val boardModel: BoardModel) {
    val hasToChooseForJoker: MutableState<Boolean> = mutableStateOf(false)
    private val undecidedJokerTile: MutableState<Pair<TileModel, TileCoordinates>?> = mutableStateOf(null)

    fun checkOpenForJokerSelection(tile: TileContent, tileCoordinates: TileCoordinates) {
        if (tile != null) {
            if (tile.letter == JokerChar) {
                hasToChooseForJoker.value = true
                undecidedJokerTile.value = Pair(tile, tileCoordinates)
            }
        }
    }

    fun removeJoker() {
        undecidedJokerTile.value?.first?.isUsedOnBoard?.value = false
        undecidedJokerTile.value?.let { boardModel.setTransient(it.second, null) }
        undecidedJokerTile.value = null
    }

    fun chooseJoker(selectedTile: TileModel) {
        undecidedJokerTile.value?.first?.isUsedOnBoard?.value = true
        undecidedJokerTile.value?.first?.displayedLetter = selectedTile.letter
        undecidedJokerTile.value = null
    }
}
