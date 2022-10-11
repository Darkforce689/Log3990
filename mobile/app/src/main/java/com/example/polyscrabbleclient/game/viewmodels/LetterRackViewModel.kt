package com.example.polyscrabbleclient.game.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.game.domain.LetterCreator
import com.example.polyscrabbleclient.game.model.TileModel
import com.example.polyscrabbleclient.game.sources.Player
import com.example.polyscrabbleclient.game.view.draganddrop.DraggableContent
import com.example.polyscrabbleclient.game.view.draganddrop.DraggableContentType

class LetterRackViewModel: ViewModel() {
    private val letterCreator = LetterCreator();
    private val player = Player()
    val tiles = mutableStateListOf (
        *player.letters.map { letter -> letterCreator.createTileFromLetter(letter) }.toTypedArray()
    )

    fun removeTile(draggableContent: DraggableContent?) {
        if (draggableContent == null || draggableContent.type !== DraggableContentType.TileModel) {
            return
        }
        tiles.remove(draggableContent as TileModel)
    }
}
