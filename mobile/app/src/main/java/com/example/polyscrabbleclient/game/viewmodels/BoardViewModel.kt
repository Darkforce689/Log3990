package com.example.polyscrabbleclient.game.viewmodels

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.game.model.BoardModel

class BoardViewModel : ViewModel() {
    var board: BoardModel = BoardModel()
}
