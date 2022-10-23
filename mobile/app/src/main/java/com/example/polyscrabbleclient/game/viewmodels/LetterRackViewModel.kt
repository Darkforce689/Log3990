package com.example.polyscrabbleclient.game.viewmodels

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.game.model.TileModel
import com.example.polyscrabbleclient.game.sources.GameRepository
import com.example.polyscrabbleclient.game.view.draganddrop.DraggableContent
import com.example.polyscrabbleclient.game.view.draganddrop.DraggableContentType

class LetterRackViewModel: ViewModel() {
    private val game = GameRepository.game

    private val user = game.user

    fun getTiles(): MutableList<TileModel> {
        return user.value?.letters ?: mutableListOf()
    }

    fun removeTile(draggableContent: DraggableContent?) {
        if (draggableContent == null || draggableContent.type !== DraggableContentType.TileModel) {
            return
        }
        user.value?.letters?.remove(draggableContent as TileModel)
    }
}
