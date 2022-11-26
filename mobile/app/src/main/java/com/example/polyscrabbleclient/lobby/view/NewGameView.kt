package com.example.polyscrabbleclient.lobby.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.polyscrabbleclient.lobby.viewmodels.JoinGameViewModel
import com.example.polyscrabbleclient.lobby.viewmodels.NewGameViewModel
import com.example.polyscrabbleclient.navigateTo
import com.example.polyscrabbleclient.ui.theme.*

@Composable
fun NewGameScreen(
    navController: NavController,
    createGameViewModel: CreateGameViewModel,
    viewModel: NewGameViewModel = viewModel()
) {
    val createGameDialogOpened = remember {
        viewModel.createGameDialogOpened
    }
    val joinGameDialogOpened = remember {
        viewModel.joinGameDialogOpened
    }
    val watchGameDialogOpened = remember {
        viewModel.watchGameDialogOpened
    }
    val enterGamePasswordDialogOpened = remember {
        viewModel.enterGamePasswordDialogOpened
    }
    val hasJustConfirmedJoin = remember {
        viewModel.hasJustConfirmedJoin
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
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colors.error.copy(0.8f),
                    ),
                    checked = isToggled.value,
                    onCheckedChange = { value ->
                        isToggled.value = value
                        createGameViewModel.gameMode.value =
                            if (isToggled.value) GameMode.Magic else GameMode.Classic
                    },
                )

                LobbyButton(createGameDialogOpened, create_game_multiplayers)
                CreateGameModal(
                    navController,
                    createGameDialogOpened,
                    createGameViewModel
                )

                val joinGameViewModel = JoinGameViewModel()

                LobbyButton(
                    joinGameDialogOpened,
                    join_game_multiplayers,
                    MaterialTheme.colors.secondary
                )
                JoinAGameModal(
                    joinGameDialogOpened,
                    enterGamePasswordDialogOpened,
                    joinGameViewModel,
                    navController,
                    createGameViewModel.pendingGames,
                )

                EnterPasswordModal(
                    enterGamePasswordDialogOpened,
                    joinGameViewModel,
                    navController,
                )

                BadPasswordModal(
                    hasJustConfirmedJoin,
                    joinGameViewModel,
                    navController,
                )

                LobbyButton(
                    watchGameDialogOpened,
                    watch_game_multiplayers,
                    MaterialTheme.colors.secondary
                )
                WatchAGameModal(
                    watchGameDialogOpened,
                    joinGameViewModel,
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
fun EnterPasswordModal(
    enterGamePasswordDialogOpened: MutableState<Boolean>,
    viewModel: JoinGameViewModel,
    navController: NavController
) {
    if (enterGamePasswordDialogOpened.value) {
        ModalView(
            closeModal = { result ->
                enterGamePasswordDialogOpened.value = false
                if (result == ModalResult.Primary) {
                    viewModel.joinGame(navController)
                }
            },
            title = EnterPasswordFR
        ) { modalButtons ->
            EnterPasswordView(viewModel, navController) { modalActions ->
                modalButtons(modalActions)
            }
        }
    }
}

@Composable
fun BadPasswordModal(
    hasJustConfirmedJoin: MutableState<Boolean?>,
    viewModel: JoinGameViewModel,
    navController: NavController
) {
    if (hasJustConfirmedJoin.value == false) {
        ModalView(
            closeModal = {
                hasJustConfirmedJoin.value = null
            },
            title = BadPasswordFR
        ) { modalButtons ->
            BadPasswordView(navController, viewModel) { modalActions ->
                modalButtons(modalActions)
            }
        }
    }
}


@Composable
private fun LobbyButton(
    actionEffect: MutableState<Boolean>,
    label: String,
    backgroundColor: Color = MaterialTheme.colors.primary
) {
    Button(
        modifier = Modifier.padding(10.dp),
        onClick = { actionEffect.value = true },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor
        ),
    ) {
        Text(text = label)
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
                    navigateTo(NavPage.WaitingRoom, navController)
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
    enterGamePasswordDialogOpened: MutableState<Boolean>,
    viewModel: JoinGameViewModel,
    navController: NavController,
    pendingGames: MutableState<LobbyGamesList?>
) {
    if (joinGameDialogOpened.value) {
        ModalView(
            closeModal = { result ->
                joinGameDialogOpened.value = false
                if (result == ModalResult.Primary) {
                    if (viewModel.isGameProtected()) {
                        enterGamePasswordDialogOpened.value = true
                    } else {
                        viewModel.joinGame(navController)
                    }
                }
            },
            title = joinAGameFR,
            maxWidth = 950.dp
        ) { modalButtons ->
            JoinGameView(
                pendingGames,
                viewModel,
            ) { modalActions ->
                modalButtons(modalActions)
            }
        }
    }
}

@Composable
private fun WatchAGameModal(
    watchGameDialogOpened: MutableState<Boolean>,
    viewModel: JoinGameViewModel,
    navController: NavController,
    observableGames: MutableState<LobbyGamesList?>
) {
    if (watchGameDialogOpened.value) {
        ModalView(
            closeModal = { result ->
                watchGameDialogOpened.value = false
                if (result == ModalResult.Primary) {
                    viewModel.joinGame(navController)
                }
            },
            title = watchAGameFR,
            maxWidth = 950.dp
        ) { modalButtons ->
            JoinGameView(
                observableGames,
                viewModel,
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
