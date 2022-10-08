package com.example.polyscrabbleclient.game.viewmodels

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.game.model.GameModel
import com.example.polyscrabbleclient.game.sources.Player

class GameViewModel : ViewModel() {
    var game: GameModel = GameModel().apply {
        val p = Player()
        addPlayer(p)
    }

    val user = game.getPlayer(0)
}
