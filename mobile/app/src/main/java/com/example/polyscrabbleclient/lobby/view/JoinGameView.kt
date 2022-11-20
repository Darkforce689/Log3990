package com.example.polyscrabbleclient.lobby.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.polyscrabbleclient.NavPage
import com.example.polyscrabbleclient.lobby.domain.ModalActions
import com.example.polyscrabbleclient.lobby.sources.LobbyGameId
import com.example.polyscrabbleclient.lobby.sources.LobbyGamesList
import com.example.polyscrabbleclient.lobby.viewmodels.JoinGameViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

enum class LobbyGameType {
    PendingGame,
    ObservableGame
}

@Composable
fun JoinGameView(
    navController: NavController,
    lobbyGames: MutableState<LobbyGamesList?>,
    modalButtons: @Composable (modalActions: ModalActions) -> Unit
) {
    val viewModel: JoinGameViewModel = viewModel()

    EvenlySpacedRowContainer {
        Box {
            LobbyGamesView(
                lobbyGames,
                viewModel.selectedGameMode.value,
                navigateToGameScreen(viewModel, navController)
            ) { modalActions ->
                modalButtons(modalActions)
            }
        }
    }
}

@Composable
private fun navigateToGameScreen(
    viewModel: JoinGameViewModel,
    navController: NavController
) = { lobbyGameIndex: LobbyGameId ->
    viewModel.joinGame(lobbyGameIndex) {
        CoroutineScope(IO).launch {
            launch(Dispatchers.Main) {
                navController.navigate(NavPage.GamePage.label) {
                    launchSingleTop = true
                }
            }
        }
    }
}


@Composable
fun EvenlySpacedRowContainer(content: @Composable RowScope.() -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        content()
    }
}

@SuppressLint("UnrememberedMutableState", "MutableCollectionMutableState")
@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun JoinGamePreview() {
    JoinGameView(
        rememberNavController(),
        mutableStateOf(arrayListOf()),
    ) {}
}
