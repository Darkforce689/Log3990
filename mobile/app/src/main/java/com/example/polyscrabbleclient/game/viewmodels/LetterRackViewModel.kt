package com.example.polyscrabbleclient.game.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.game.domain.LetterCreator
import com.example.polyscrabbleclient.game.sources.GameRepository
import com.example.polyscrabbleclient.game.sources.Player
import com.example.polyscrabbleclient.game.view.draganddrop.DraggableContent
import com.example.polyscrabbleclient.game.view.draganddrop.DraggableContentType

class LetterRackViewModel: ViewModel() {
    private val gameRepository = GameRepository
    private val letterCreator = LetterCreator();
    private val player = Player("A")
    val tiles = mutableStateListOf (
        *player.letters.map { letter -> letterCreator.createTileFromLetter(letter) }.toTypedArray()
    )

    fun removeTile(draggableContent: DraggableContent?) {
        if (draggableContent == null || draggableContent.type !== DraggableContentType.TileModel) {
            return
        }
        // TODO : REMOVE CORRECT LETTER INDEX
        // tiles.remove(draggableContent as TileModel)
    }
}
