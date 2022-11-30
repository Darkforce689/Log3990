package com.example.polyscrabbleclient.lobby.viewmodels

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.polyscrabbleclient.NavPage
import com.example.polyscrabbleclient.invitations.models.GameInviteArgs
import com.example.polyscrabbleclient.invitations.utils.GameInviteBroker
import com.example.polyscrabbleclient.lobby.sources.LobbyRepository
import com.example.polyscrabbleclient.navigateTo
import com.example.polyscrabbleclient.user.UserRepository

class WaitingForOtherPlayersViewModel : ViewModel() {
    private val lobby = LobbyRepository
    val hostHasJustQuitTheGame = LobbyRepository.model.hostHasJustQuitTheGame

    fun launchGame(navigateToGameScreen: () -> Unit) {
        lobby.emitLaunchGame(navigateToGameScreen)
        GameInviteBroker.destroyInvite() // TODO Change if join server sends join confirmation
    }

    fun getPlayersProfil(): List<Pair<String, String>> {
        var playersProfil: List<Pair<String, String>> = listOf()
        var playerNames = lobby.model.pendingGamePlayerNames.value
        val nPlayers = playerNames.size
        val totalPlayers = lobby.model.maxPlayerInWaitingGame.value ?: 4
        val botNames = lobby.model.botNames.value
        if (!botNames.isNullOrEmpty()) {
            playerNames = playerNames + botNames.slice(nPlayers - 1 until totalPlayers - 1)
        }
        val thread = Thread {
            for (playerName in playerNames) {
                UserRepository.getUserByName(playerName) {
                    playersProfil = playersProfil.plus(Pair(it.name, it.avatar))
                }
            }
        }
        thread.start()
        thread.join()
        return playersProfil
    }

    fun getGameInviteArgs(): GameInviteArgs {
        val id = LobbyRepository.model.currentPendingGameId.value
        if (id == null) {
            return GameInviteArgs(id = "", password = "")
        }
        val password = LobbyRepository.model.password.value
        return GameInviteArgs(id = id, password = password)
    }

    fun canInvite(): Boolean {
        return canLaunchGame()
    }

    fun isHost(): Boolean {
        return lobby.model.isPendingGameHost.value
    }

    fun canLaunchGame(): Boolean {
        return isHost() && lobby.model.currentPendingGameId.value !== null
    }

    fun leaveLobbyGame(navController: NavController) {
        lobby.leaveLobbyGame()
        navigateTo(NavPage.MainPage, navController)
        GameInviteBroker.destroyInvite() // TODO Change if join server sends join confirmation
    }

    fun getPendingGamePlayerNames(): List<String> {
        return lobby.model.pendingGamePlayerNames.value
    }

    fun isHost(playerName: String): Boolean {
        return playerName == getPendingGamePlayerNames()[0]
    }

    fun isGamePrivate(): Boolean {
        return lobby.model.isGamePrivate.value
    }

    fun canAcceptRefusePlayers(): Boolean {
        return isGamePrivate() && isHost()
    }

    fun getCandidatePlayerNames(): List<String> {
        return lobby.model.candidatePlayerNames.value
    }

    fun kick(playerName: String) {
        LobbyRepository.kickPlayer(playerName)
    }

    fun accept(playerName: String) {
        LobbyRepository.acceptPlayer(playerName)
    }

    fun refuse(playerName: String) {
        LobbyRepository.refusePlayer(playerName)
    }
}
