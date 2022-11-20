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
import androidx.navigation.NavController
import com.example.polyscrabbleclient.lobby.domain.ModalResult
import com.example.polyscrabbleclient.lobby.sources.GameMode
import com.example.polyscrabbleclient.lobby.sources.LobbyGamesList
import com.example.polyscrabbleclient.lobby.view.createGame.CreateGameModalContent
import com.example.polyscrabbleclient.lobby.viewmodels.CreateGameViewModel
import com.example.polyscrabbleclient.ui.theme.*

@Composable
fun NewGameScreen(
    navController: NavController,
    createGameViewModel: CreateGameViewModel,
) {
    val createGameDialogOpened = remember { mutableStateOf(false) }
    val joinGameDialogOpened = remember { mutableStateOf(false) }
    val watchGameDialogOpened = remember { mutableStateOf(false) }
    val waitingForOtherPlayersDialogOpened = remember { mutableStateOf(false) }

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
                    createGameDialogOpened,
                    waitingForOtherPlayersDialogOpened,
                    createGameViewModel
                )

                JoinAGameModal(
                    joinGameDialogOpened,
                    waitingForOtherPlayersDialogOpened,
                    navController,
                    createGameViewModel.pendingGames
                )

                WatchAGameModal(
                    watchGameDialogOpened,
                    waitingForOtherPlayersDialogOpened,
                    navController,
                    createGameViewModel.observableGames
                )

                WaitingForOtherPlayersModal(waitingForOtherPlayersDialogOpened, navController)

                HostHasJustQuitModal(createGameViewModel, waitingForOtherPlayersDialogOpened)
            }
        }
    }
}

@Composable
private fun CreateGameModal(
    createGameDialogOpened: MutableState<Boolean>,
    waitingForOtherPlayersDialogOpened: MutableState<Boolean>,
    createGameViewModel: CreateGameViewModel
) {
    if (createGameDialogOpened.value) {
        ModalView(
            closeModal = { result ->
                createGameDialogOpened.value = false
                if (result == ModalResult.Primary) {
                    waitingForOtherPlayersDialogOpened.value = true
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
    waitingForOtherPlayersDialogOpened: MutableState<Boolean>,
    navController: NavController,
    pendingGames: MutableState<LobbyGamesList?>
) {
    if (joinGameDialogOpened.value) {
        ModalView(
            closeModal = { result ->
                joinGameDialogOpened.value = false
                if (result == ModalResult.Primary) {
                    waitingForOtherPlayersDialogOpened.value = true
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
    waitingForOtherPlayersDialogOpened: MutableState<Boolean>,
    navController: NavController,
    observableGames: MutableState<LobbyGamesList?>
) {
    if (watchGameDialogOpened.value) {
        ModalView(
            closeModal = { result ->
                watchGameDialogOpened.value = false
                if (result == ModalResult.Primary) {
                    waitingForOtherPlayersDialogOpened.value = true
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
private fun WaitingForOtherPlayersModal(
    waitingForOtherPlayersDialogOpened: MutableState<Boolean>,
    navController: NavController
) {
    if (waitingForOtherPlayersDialogOpened.value) {
        ModalView(
            closeModal = { waitingForOtherPlayersDialogOpened.value = false },
            title = waitingForOtherPlayersFR,
            hasSpinner = true
        ) { modalButtons ->
            WaitingForOtherPlayersView(navController = navController) { modalActions ->
                modalButtons(modalActions)
            }
        }
    }
}

@Composable
private fun HostHasJustQuitModal(
    createGameViewModel: CreateGameViewModel,
    waitingForOtherPlayersDialogOpened: MutableState<Boolean>
) {
    if (createGameViewModel.hostHasJustQuitTheGame.value) {
        ModalView(
            closeModal = {
                createGameViewModel.hostHasJustQuitTheGame.value = false
                waitingForOtherPlayersDialogOpened.value = false
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
