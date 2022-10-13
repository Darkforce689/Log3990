package com.example.polyscrabbleclient.game.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.game.model.GameModel
import com.example.polyscrabbleclient.game.sources.*


class GameViewModel: ViewModel() {
    var game: GameModel = GameModel()
    var remainingLettersCount = mutableStateOf(88)
    var turnRemainingTime = mutableStateOf(14)
    var turnTotalTime = mutableStateOf(60)
}
