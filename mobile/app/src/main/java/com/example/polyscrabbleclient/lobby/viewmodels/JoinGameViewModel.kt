package com.example.polyscrabbleclient.lobby.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.polyscrabbleclient.NavPage
import com.example.polyscrabbleclient.lobby.sources.GameMode
import com.example.polyscrabbleclient.lobby.sources.JoinGame
import com.example.polyscrabbleclient.lobby.sources.LobbyRepository
import com.example.polyscrabbleclient.lobby.sources.OnlineGameSettings
import com.example.polyscrabbleclient.navigateTo

enum class LobbyGameType {
    Observable,
    Pending
}

class JoinGameViewModel : ViewModel() {
    val enteredPassword = mutableStateOf("")

    val selectedLobbyGame = LobbyRepository.model.selectedLobbyGame

    val hasJustConfirmedJoin = LobbyRepository.model.hasJustConfirmedJoin

    fun isGameProtected(): Boolean {
        return selectedLobbyGame.value?.isProtected() ?: false
    }

    fun isGameSelected(lobbyGame: OnlineGameSettings): Boolean {
        return selectedLobbyGame.value == lobbyGame
    }

    fun getSelectedGameMode(): GameMode {
        return LobbyRepository.model.selectedGameMode.value
    }

    fun toggleSelectedGame(lobbyGame: OnlineGameSettings) {
        selectedLobbyGame.value =
            if (isGameSelected(lobbyGame)) {
                null
            } else {
                lobbyGame
            }
    }

    fun leaveLobbyGame(navController: NavController) {
        LobbyRepository.leaveLobbyGame()
        navigateTo(NavPage.MainPage, navController)
    }

    fun joinGame(
        navController: NavController
    ) {
        if (selectedLobbyGame.value === null) {
            return
        }
        LobbyRepository.emitJoinGame(
            JoinGame(selectedLobbyGame.value!!.id, enteredPassword.value),
            navController
        )
    }
}
