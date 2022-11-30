package com.example.polyscrabbleclient.lobby.view

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.polyscrabbleclient.NavPage
import com.example.polyscrabbleclient.account.components.Avatar
import com.example.polyscrabbleclient.getAssetsId
import com.example.polyscrabbleclient.invitations.components.InviteUserToGameModal
import com.example.polyscrabbleclient.lobby.sources.LobbyRepository
import com.example.polyscrabbleclient.lobby.viewmodels.WaitingForOtherPlayersViewModel
import com.example.polyscrabbleclient.navigateTo
import com.example.polyscrabbleclient.ui.theme.*
import com.example.polyscrabbleclient.user.UserRepository
import com.example.polyscrabbleclient.user.UserRepository.isBotName
import com.example.polyscrabbleclient.utils.SubTitleView
import com.example.polyscrabbleclient.utils.constants.NoAvatar

@Composable
fun WaitingForOtherPlayersView(
    navController: NavController,
    viewModel: WaitingForOtherPlayersViewModel = viewModel()
) {
    var isInviteModalOpened by remember {
        mutableStateOf(false)
    }
    val playersProfiles: List<Pair<String, String>> = viewModel.getPlayersProfil()
    CenteredContainer {
        Card(
            modifier = Modifier
                .width(420.dp)
                .height(375.dp),
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
                            IconButton(
                                onClick = { isInviteModalOpened = true },
                                Modifier.background(Color.LightGray, CircleShape)
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
                        playersProfiles.forEach {
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
private fun PlayerView(
    playersProfil: Pair<String, String>,
    playerSideElements: @Composable () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(0.dp, 2.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val (name, avatar) = playersProfil
        PlayerAndAvatar(name, avatar)
        playerSideElements()
    }
}

@Composable
private fun PlayerAndAvatar(playerName: String, avatar: String) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(0.6f)
    ) {
        Avatar(modifier = Modifier.size(40.dp), avatarId = getAssetsId(name = avatar))
        Text(
            text = playerName,
            modifier = Modifier.padding(5.dp),
            fontSize = 18.sp
        )
    }
}


@Composable
private fun ConfirmedPlayerView(
    playersProfil: Pair<String, String>,
    viewModel: WaitingForOtherPlayersViewModel
) {
    val (name) = playersProfil
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerView(playersProfil) {
            if (viewModel.isHost(name)) {
                HostPlayerView()
            } else {
                if (viewModel.isHost() && !isBotName(name)) {
                    KickPlayerView { viewModel.kick(name) }
                }
            }
        }
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
    IconButton(
        onClick = { action?.invoke() },
        enabled = action !== null,
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
        Spacer(modifier = Modifier.size(20.dp))
        Card(
            modifier = Modifier
                .width(400.dp)
                .height(250.dp),
        ) {
            Column(
                Modifier.padding(10.dp)
            ) {
                SubTitleView(CandidatePlayers)
                LazyColumn {
                    val playerNames = viewModel.getCandidatePlayerNames()
                    items(playerNames.size) {
                        val playerName = playerNames[it]
                        var avatar = NoAvatar
                        LaunchedEffect(key1 = playerName) {
                            UserRepository.getUserByName(playerName) { user ->
                                avatar = user.avatar
                            }
                        }
                        CandidatePlayerView(
                            Pair(playerName, avatar),
                            { viewModel.accept(playerName) },
                            { viewModel.refuse(playerName) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CandidatePlayerView(
    playerProfil: Pair<String, String>,
    accept: () -> Unit,
    refuse: () -> Unit,
) {
    PlayerView(playerProfil) {
        AcceptPlayerView { accept() }
        RefusePlayerView { refuse() }
    }
}

@Preview(showBackground = true)
@Composable
fun WaitingForOtherPlayersPreviewReadOnly() {
    val vm = WaitingForOtherPlayersViewModel()
    LobbyRepository.model.isGamePrivate.value = true
    LobbyRepository.model.isPendingGameHost.value = false
    LobbyRepository.model.pendingGamePlayerNames.value =
        listOf("Player", "Player2", "PlayerButHeDecidedToMessTheUIEvenFurther")
    LobbyRepository.model.candidatePlayerNames.value =
        listOf("Candidate", "Candidate2", "CandidateButHeDecidedToMessTheUIEvenFurther")
    WaitingForOtherPlayersView(rememberNavController(), vm)
}

@Preview(showBackground = true)
@Composable
fun PopularWaitingForOtherPlayersPreviewReadOnly() {
    val vm = WaitingForOtherPlayersViewModel()
    LobbyRepository.model.isGamePrivate.value = true
    LobbyRepository.model.isPendingGameHost.value = true
    LobbyRepository.model.pendingGamePlayerNames.value =
        listOf("Host", "PlayerB", "PayerC", "PlayerD")
    LobbyRepository.model.candidatePlayerNames.value =
        (1..100).map { "Player$it" }
    WaitingForOtherPlayersView(rememberNavController(), vm)
}

@Preview(showBackground = true)
@Composable
fun WaitingForOtherPlayersPreview() {
    val vm = WaitingForOtherPlayersViewModel()
    LobbyRepository.model.isGamePrivate.value = true
    LobbyRepository.model.isPendingGameHost.value = true
    LobbyRepository.model.pendingGamePlayerNames.value =
        listOf("Player", "Player2", "PlayerButHeDecidedToMessTheUIEvenFurther")
    LobbyRepository.model.candidatePlayerNames.value =
        listOf("Candidate", "Candidate2", "CandidateButHeDecidedToMessTheUIEvenFurther")
    WaitingForOtherPlayersView(rememberNavController(), vm)
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun DarkWaitingForOtherPlayersPreview() {
    PolyScrabbleClientTheme(isDarkTheme = mutableStateOf(true)) {
        WaitingForOtherPlayersPreview()
    }
}
