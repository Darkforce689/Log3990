package com.example.polyscrabbleclient.lobby.view

import androidx.compose.foundation.layout.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.polyscrabbleclient.NavPage
import com.example.polyscrabbleclient.lobby.viewmodels.LobbyViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


@Composable
fun LobbyScreen(navController: NavController) {
    val viewModel: LobbyViewModel = viewModel()

    EvenlySpacedRowContainer {
        Box {
            PendingGamesView(viewModel.pendingGames) { pendingGameIndex ->
                viewModel.joinGame(pendingGameIndex) {
                    CoroutineScope(IO).launch {
                        launch(Dispatchers.Main) {
                            navController.navigate(NavPage.GamePage.label) {
                                launchSingleTop = true
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun EvenlySpacedRowContainer(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        content()
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun LobbyScreenPreview() {
    LobbyScreen(navController = rememberNavController())
}
