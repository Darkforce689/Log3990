package com.example.polyscrabbleclient.lobby.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.polyscrabbleclient.NavPage
import com.example.polyscrabbleclient.lobby.domain.ModalResult
import com.example.polyscrabbleclient.lobby.sources.GameMode
import com.example.polyscrabbleclient.lobby.sources.LobbyGamesList
import com.example.polyscrabbleclient.lobby.view.createGame.CreateGameModalContent
import com.example.polyscrabbleclient.lobby.viewmodels.CreateGameViewModel
import com.example.polyscrabbleclient.lobby.viewmodels.NewGameViewModel
import com.example.polyscrabbleclient.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun NewGameScreen(
    navController: NavController,
    createGameViewModel: CreateGameViewModel,
) {
    val viewModel: NewGameViewModel = viewModel()
    val createGameDialogOpened = remember {
        viewModel.createGameDialogOpened
    }
    val joinGameDialogOpened = remember {
        viewModel.joinGameDialogOpened
    }
    val watchGameDialogOpened = remember {
        viewModel.watchGameDialogOpened
    }

    val isToggled = remember { mutableStateOf(false) }
    val gameText = if (isToggled.value) magic_cards else classic

    CenteredContainer {
        Card(
            modifier = Modifier
                .width(400.dp)
                .height(400.dp)
        ) {
            CenteredContainer {
                Text(text = new_game, fontSize = 25.sp)
                Text(text = "$game_mode : $gameText")
                Switch(
                    checked = isToggled.value,
                    onCheckedChange = { value ->
                        isToggled.value = value
                        createGameViewModel.gameMode.value =
                            if (isToggled.value) GameMode.Magic else GameMode.Classic
                    },
                )

                Button(
                    modifier = Modifier.padding(10.dp),
                    onClick = { createGameDialogOpened.value = true }
                ) {
                    Text(text = create_game_multiplayers)
                }

                Button(
                    modifier = Modifier.padding(10.dp),
                    onClick = { joinGameDialogOpened.value = true }
                ) {
                    Text(text = join_game_multiplayers)
                }

                Button(
                    modifier = Modifier.padding(10.dp),
                    onClick = { watchGameDialogOpened.value = true }
                ) {
                    Text(text = watch_game_multiplayers)
                }

                CreateGameModal(
                    navController,
                    createGameDialogOpened,
                    createGameViewModel
                )

                JoinAGameModal(
                    joinGameDialogOpened,
                    navController,
                    createGameViewModel.pendingGames
                )

                WatchAGameModal(
                    watchGameDialogOpened,
                    navController,
                    createGameViewModel.observableGames
                )
                HostHasJustQuitModal(createGameViewModel.hostHasJustQuitTheGame) {
                    createGameViewModel.hostHasJustQuitTheGame.value = false
                }
            }
        }
    }
}


@Composable
private fun CreateGameModal(
    navController: NavController,
    createGameDialogOpened: MutableState<Boolean>,
    createGameViewModel: CreateGameViewModel
) {
    if (createGameDialogOpened.value) {
        ModalView(
            closeModal = { result ->
                createGameDialogOpened.value = false
                if (result == ModalResult.Primary) {
                    navigateToWaitingScreen(navController)

                }
            },
            title = new_game_creation
        ) { modalButtons ->
            CreateGameModalContent(createGameViewModel) { modalActions ->
                modalButtons(modalActions)
            }
        }
    }
}

@Composable
private fun JoinAGameModal(
    joinGameDialogOpened: MutableState<Boolean>,
    navController: NavController,
    pendingGames: MutableState<LobbyGamesList?>
) {
    if (joinGameDialogOpened.value) {
        ModalView(
            closeModal = { result ->
                joinGameDialogOpened.value = false
                if (result == ModalResult.Primary) {
                    navigateToWaitingScreen(navController)
                }
            },
            title = joinAGameFR,
            maxWidth = 950.dp
        ) { modalButtons ->
            JoinGameView(
                navController,
                pendingGames,
            ) { modalActions ->
                modalButtons(modalActions)
            }
        }
    }
}

@Composable
private fun WatchAGameModal(
    watchGameDialogOpened: MutableState<Boolean>,
    navController: NavController,
    observableGames: MutableState<LobbyGamesList?>
) {
    if (watchGameDialogOpened.value) {
        ModalView(
            closeModal = { result ->
                watchGameDialogOpened.value = false
                if (result == ModalResult.Primary) {
                    navigateToWaitingScreen(navController)
                }
            },
            title = watchAGameFR,
            maxWidth = 950.dp
        ) { modalButtons ->
            JoinGameView(
                navController,
                observableGames,
            ) { modalActions ->
                modalButtons(modalActions)
            }
        }
    }
}

@Composable
fun HostHasJustQuitModal(
    hostHasJustQuitTheGame: MutableState<Boolean>,
    onCancel: () -> Unit,
) {
    if (hostHasJustQuitTheGame.value) {
        ModalView(
            closeModal = {
                onCancel()
            },
            title = hostQuitGameFR
        ) { modalButtons ->
            HostQuitGameView { modalActions ->
                modalButtons(modalActions)
            }
        }
    }
}

@Composable
fun CenteredContainer(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        content()
    }
}

private fun navigateToWaitingScreen(navController: NavController) {
    CoroutineScope(Dispatchers.IO).launch {
        launch(Dispatchers.Main) {
            navController.navigate(NavPage.WaitingRoom.label) {
                launchSingleTop = true
            }
        }
    }
}
