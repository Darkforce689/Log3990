package com.example.polyscrabbleclient.lobby.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.polyscrabbleclient.NavPage
import com.example.polyscrabbleclient.lobby.sources.GameMode
import com.example.polyscrabbleclient.lobby.sources.JoinGame
import com.example.polyscrabbleclient.lobby.sources.LobbyGameId
import com.example.polyscrabbleclient.lobby.sources.LobbyRepository
import com.example.polyscrabbleclient.navigateTo

class JoinGameViewModel : ViewModel() {
    val password = mutableStateOf("")

    val selectedGameMode = LobbyRepository.model.selectedGameMode

    val selectedGameId = mutableStateOf<LobbyGameId?>(null)

    fun isGameProtected(): Boolean {
        return LobbyRepository.model.isGameProtected.value
    }

    fun isGameSelected(lobbyGameId: LobbyGameId): Boolean {
        return selectedGameId.value == lobbyGameId
    }

    fun getSelectedGameMode(): GameMode {
        return LobbyRepository.model.selectedGameMode.value
    }

    fun toggleSelectedGame(lobbyGameId: LobbyGameId) {
        selectedGameId.value =
            if (isGameSelected(lobbyGameId)) {
                null
            } else {
                lobbyGameId
            }
    }

    fun joinGame(
        navController: NavController
    ) {
        if (selectedGameId.value === null) {
            return
        }
        LobbyRepository.emitJoinGame(
            JoinGame(selectedGameId.value!!)
        ) { navigateTo(NavPage.GamePage, navController) }
    }
}
