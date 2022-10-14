package com.example.polyscrabbleclient.game.viewmodels

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.game.sources.GameRepository
import com.example.polyscrabbleclient.game.view.draganddrop.DraggableContent
import com.example.polyscrabbleclient.game.view.draganddrop.DraggableContentType

class LetterRackViewModel: ViewModel() {
    private val game = GameRepository.game
    val tiles = game.getUser()?.letters

    fun removeTile(draggableContent: DraggableContent?) {
        if (draggableContent == null || draggableContent.type !== DraggableContentType.TileModel) {
            return
        }
        // TODO : REMOVE CORRECT LETTER INDEX
        // tiles.remove(draggableContent as TileModel)
    }
}
