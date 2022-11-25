package com.example.polyscrabbleclient.lobby.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.rounded.StarRate
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.polyscrabbleclient.NavPage
import com.example.polyscrabbleclient.invitations.components.InviteUserToGameModal
import com.example.polyscrabbleclient.lobby.viewmodels.WaitingForOtherPlayersViewModel
import com.example.polyscrabbleclient.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun WaitingForOtherPlayersView(
    navController: NavController,
) {
    val viewModel: WaitingForOtherPlayersViewModel = viewModel()
    var isInviteModalOpened by remember {
        mutableStateOf(false)
    }
    CenteredContainer {
        Card(
            modifier = Modifier
                .width(400.dp)
                .height(350.dp),
        ) {
            Column(
                Modifier.padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SpinnerView()
                            Text(
                                text = waitingForOtherPlayersFR,
                                fontStyle = MaterialTheme.typography.h4.fontStyle,
                                modifier = Modifier.padding(5.dp),
                                fontSize = 22.sp
                            )
                        }
                        if (viewModel.canInvite()) {
                            Button(
                                onClick = { isInviteModalOpened = true },
                                shape = CircleShape,
                                modifier = Modifier.padding(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = null,
                                )
                            }
                        }
                    }
                    Text(
                        text = players_in_game, Modifier.padding(5.dp), fontSize = 20.sp
                    )
                    Column(Modifier.padding(5.dp)) {
                        viewModel.getPendingGamePlayerNames().forEach {
                            Row(
                                Modifier.fillMaxWidth(0.3f),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Todo add ICON -> in other PR (JU)
                                Text(
                                    text = "> $it",
                                    modifier = Modifier.padding(5.dp),
                                    fontSize = 18.sp
                                )
                                if (viewModel.isHost(it)) {
                                    Icon(
                                        modifier = Modifier.size(30.dp),
                                        imageVector = Icons.Rounded.StarRate,
                                        contentDescription = null,
                                        tint = MaterialTheme.colors.secondary
                                    )
                                }
                            }

                        }
                    }
                    // TODO : ACCEPT / KICK PLAYERS
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(

                        onClick = {
                            viewModel.leavePendingGame();
                            navigateToHomeScreen(navController)
                        }
                    ) { Text(text = cancelButtonFR) }

                    Button(
                        enabled = viewModel.canLaunchGame(),
                        onClick = {
                            viewModel.launchGame {
                                navigateToGameScreen(
                                    navController
                                )
                            }
                        }
                    ) { Text(text = launchGameButtonFR) }

                }
            }
        }
        HostHasJustQuitModal(viewModel.hostHasJustQuitTheGame) { navigateToHomeScreen(navController) }

        // TODO if waiting modal stop recomposing notify oli
        InviteUserToGameModal(
            inviteArgs = viewModel.getGameInviteArgs(),
            isOpened = isInviteModalOpened,
            onClose = { isInviteModalOpened = false }
        )
    }
}

private fun navigateToHomeScreen(navController: NavController) {
    CoroutineScope(Dispatchers.IO).launch {
        launch(Dispatchers.Main) {
            navController.navigate(NavPage.MainPage.label) {
                launchSingleTop = true
            }
        }
    }
}

private fun navigateToGameScreen(navController: NavController) {
    CoroutineScope(Dispatchers.IO).launch {
        launch(Dispatchers.Main) {
            navController.navigate(NavPage.GamePage.label) {
                launchSingleTop = true
            }
        }
    }
}


