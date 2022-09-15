package com.example.polyscrabbleclient.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel


class TileViewModel : ViewModel() {
    var isSelected by mutableStateOf(false)
        private set

    var clicked: () -> Unit = { isSelected = !isSelected }
}

