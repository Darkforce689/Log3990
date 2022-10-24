package com.example.polyscrabbleclient.lobby.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.polyscrabbleclient.NavPage
import com.example.polyscrabbleclient.lobby.sources.GameMode
import com.example.polyscrabbleclient.lobby.view.createGame.CreateGameModalContent
import com.example.polyscrabbleclient.lobby.viewmodels.CreateGameViewModel
import com.example.polyscrabbleclient.page.headerbar.view.HeaderBar
import com.example.polyscrabbleclient.ui.theme.*

@Composable
fun NewGameScreen(
    navController: NavController,
    createGameViewModel: CreateGameViewModel
) {
    val dialogState = remember { mutableStateOf(false) }
    val isToggled = remember { mutableStateOf(false) }
    val gameText = if (isToggled.value) magic_cards else classic
    HeaderBar(navController)
    CenteredContainer {
        Card(
            modifier = Modifier
                .width(400.dp)
                .height(300.dp)
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
                        println(createGameViewModel.gameMode.value)
                    },
                )
                Button(onClick = { dialogState.value = true }) {
                    Text(text = create_game_multiplayers)
                }
                Button(
                    modifier = Modifier.padding(20.dp),
                    onClick = {
                        navController.navigate(NavPage.LobbyPage.label) {
                            launchSingleTop = true
                        }
                    }
                ) {
                    Text(text = join_game_multiplayers)
                }
                if (dialogState.value) {
                    Dialog(
                        onDismissRequest = { },
                        content = {
                            CreateGameModalContent(
                                updateState = { value -> dialogState.value = value },
                                createGameViewModel
                            )
                        }
                    )
                }
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
