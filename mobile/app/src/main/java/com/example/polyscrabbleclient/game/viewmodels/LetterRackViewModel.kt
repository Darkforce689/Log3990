package com.example.polyscrabbleclient.game.viewmodels

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.game.model.TileModel
import com.example.polyscrabbleclient.game.sources.GameRepository
import com.example.polyscrabbleclient.game.view.draganddrop.DraggableContent
import com.example.polyscrabbleclient.game.view.draganddrop.DraggableContentType

class LetterRackViewModel : ViewModel() {
    val game = GameRepository.model

    fun markTileAsUsed(draggableContent: DraggableContent?): DraggableContent? {
        if (draggableContent == null || draggableContent.type !== DraggableContentType.TileModel) {
            return null
        }
        if (draggableContent !is TileModel) {
            return null
        }
        draggableContent.isUsedOnBoard.value = true
        return draggableContent
    }


    fun raiseTile(draggableContent: DraggableContent?): Boolean {
        val tile = game.userLetters.find { it === draggableContent }
        if (tile === null) {
            println("raiseTile -> did not find tile $draggableContent in letter rack")
            return false
        }
        tile.isUsedOnBoard.value = false
        return true
    }

    fun canBeDragged(tile: TileModel): () -> Boolean {
        return { game.isActivePlayer() && !tile.isUsedOnBoard.value }
    }
}
