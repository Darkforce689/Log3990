package com.example.polyscrabbleclient.game.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.game.model.GameModel
import com.example.polyscrabbleclient.game.sources.Player

class GameViewModel : ViewModel() {
    var game: GameModel = GameModel().apply {
        val p = Player()
        addPlayer(p)
    }

    var remainingLettersCount = mutableStateOf(88)
    var turnRemainingTime = mutableStateOf(2)
    var turnTotalTime = mutableStateOf(60)

    val user = game.getPlayer(0)
}
