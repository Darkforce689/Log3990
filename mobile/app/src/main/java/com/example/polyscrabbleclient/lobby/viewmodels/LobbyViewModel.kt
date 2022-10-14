package com.example.polyscrabbleclient.lobby.viewmodels

import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.lobby.sources.LobbyRepository

class LobbyViewModel: ViewModel() {
    val lobby = LobbyRepository
}
