package com.example.polyscrabbleclient.lobby.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.StarRate
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.polyscrabbleclient.NavPage
import com.example.polyscrabbleclient.invitations.components.InviteUserToGameModal
import com.example.polyscrabbleclient.lobby.sources.LobbyRepository
import com.example.polyscrabbleclient.lobby.viewmodels.WaitingForOtherPlayersViewModel
import com.example.polyscrabbleclient.ui.theme.*
import com.example.polyscrabbleclient.utils.SubTitleView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun WaitingForOtherPlayersView(
    navController: NavController,
    viewModel: WaitingForOtherPlayersViewModel = viewModel()
) {
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
                    SubTitleView(players_in_game)
                    Column(Modifier.padding(5.dp)) {
                        viewModel.getPendingGamePlayerNames().forEach {
                            ConfirmedPlayerView(it, viewModel)
                        }
                    }
                }
                WaitingForOtherPlayersButtons(viewModel, navController)
            }
        }
        CandidatePlayersView(viewModel)
        HostHasJustQuitModal(viewModel.hostHasJustQuitTheGame) {
            navigateTo(
                NavPage.MainPage,
                navController
            )
        }

        // TODO if waiting modal stop recomposing notify oli
        InviteUserToGameModal(
            inviteArgs = viewModel.getGameInviteArgs(),
            isOpened = isInviteModalOpened,
            onClose = { isInviteModalOpened = false }
        )
    }
}

@Composable
private fun ConfirmedPlayerView(
    playerName: String,
    viewModel: WaitingForOtherPlayersViewModel
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerView(playerName) {
            if (viewModel.isHost(playerName)) {
                HostPlayerView()
            } else {
                if (viewModel.isHost()) {
                    KickPlayerView { viewModel.kick(playerName) }
                }
            }
        }
    }
}

@Composable
private fun PlayerView(
    playerName: String,
    playerSideElements: @Composable () -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerAndAvatar(playerName)
        playerSideElements()
    }
}

@Composable
private fun PlayerAndAvatar(playerName: String) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Todo add ICON -> in other PR (JU)
        Text(
            text = playerName,
            modifier =
            Modifier
                .padding(5.dp)
                .fillMaxWidth(0.8f),
            fontSize = 18.sp
        )
    }
}

@Composable
private fun WaitingForOtherPlayersButtons(
    viewModel: WaitingForOtherPlayersViewModel,
    navController: NavController
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = {
                viewModel.leavePendingGame();
                navigateTo(NavPage.MainPage, navController)
            }
        ) { Text(text = cancelButtonFR) }
        Button(
            enabled = viewModel.canLaunchGame(),
            onClick = {
                viewModel.launchGame {
                    navigateTo(NavPage.GamePage, navController)
                }
            }
        ) { Text(text = launchGameButtonFR) }
    }
}

@Composable
private fun HostPlayerView() {
    PlayerSideElementView(
        Icons.Rounded.StarRate,
        MaterialTheme.colors.secondary,
        null
    )
}

@Composable
private fun KickPlayerView(
    kickPlayer: () -> Unit
) {
    PlayerSideElementView(
        Icons.Rounded.Close,
    ) {
        kickPlayer()
    }
}

@Composable
private fun AcceptPlayerView(
    acceptPlayer: () -> Unit
) {
    PlayerSideElementView(
        Icons.Rounded.Check,
    ) {
        acceptPlayer()
    }
}

@Composable
private fun RefusePlayerView(
    refusePlayer: () -> Unit
) {
    PlayerSideElementView(
        Icons.Rounded.Close,
    ) {
        refusePlayer()
    }
}

@Composable
private fun PlayerSideElementView(
    icon: ImageVector,
    color: Color = MaterialTheme.colors.primary,
    action: (() -> Unit)?,
) {
    Button(
        onClick = { action?.invoke() },
        enabled = action !== null,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent
        )
    ) {
        Icon(
            modifier = Modifier.size(30.dp),
            imageVector = icon,
            contentDescription = null,
            tint = color
        )
    }
}

@Composable
private fun CandidatePlayersView(
    viewModel: WaitingForOtherPlayersViewModel
) {
    if (viewModel.canAcceptRefusePlayers()) {
        Card(
            modifier = Modifier
                .width(400.dp)
                .height(350.dp),
        ) {
            SubTitleView(CandidatePlayers)
            viewModel.getCandidatePlayerNames().forEach {
                CandidatePlayerView(
                    it,
                    { viewModel.accept(it) },
                    { viewModel.refuse(it) }
                )
            }
        }
    }
}

@Composable
fun CandidatePlayerView(
    playerName: String,
    accept: () -> Unit,
    refuse: () -> Unit,
) {
    PlayerView(playerName) {
        AcceptPlayerView { accept() }
        RefusePlayerView { refuse() }
    }
}

private fun navigateTo(page: NavPage, navController: NavController) {
    CoroutineScope(Dispatchers.IO).launch {
        launch(Dispatchers.Main) {
            navController.navigate(page.label) {
                launchSingleTop = true
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun WaitingForOtherPlayersPreview() {
    val vm = WaitingForOtherPlayersViewModel()
    LobbyRepository.model.isGamePrivate.value = false
    LobbyRepository.model.pendingGamePlayerNames.value =
        listOf("Player", "Player2", "PlayerButHeDecidedToMessTheUIEvenFurther")
    LobbyRepository.model.candidatePlayerNames.value =
        listOf("Candidate", "Candidate2", "CandidateButHeDecidedToMessTheUIEvenFurther")
    WaitingForOtherPlayersView(rememberNavController(), vm)
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun DarkWaitingForOtherPlayersPreview() {
    PolyScrabbleClientTheme(isDarkTheme = mutableStateOf(true)) {
        WaitingForOtherPlayersPreview()
    }
}
