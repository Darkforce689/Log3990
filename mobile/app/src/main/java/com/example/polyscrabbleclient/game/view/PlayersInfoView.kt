package com.example.polyscrabbleclient.game.view

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.polyscrabbleclient.account.components.Avatar
import com.example.polyscrabbleclient.game.sources.Player
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel
import com.example.polyscrabbleclient.game.viewmodels.PlayerInfoViewModel
import com.example.polyscrabbleclient.getAssetsId
import com.example.polyscrabbleclient.ui.theme.PolyScrabbleClientTheme
import com.example.polyscrabbleclient.user.UserRepository
import com.example.polyscrabbleclient.utils.constants.NoAvatar

@Composable
fun PlayersInfoView(viewModel: GameViewModel, size: Dp = 200.dp) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        viewModel.getOrderedPlayers().forEach { player ->
            PlayerInfoView(
                player,
                size,
                PlayerInfoViewModel(),
                { player === viewModel.game.getWatchedPlayer() },
                { player === viewModel.game.getActivePlayer() }
            )
        }
    }
}

@Composable
fun PlayerInfoView(
    player: Player,
    size: Dp,
    viewModel: PlayerInfoViewModel,
    isWatchedPlayer: () -> Boolean,
    isActivePlayer: () -> Boolean,
) {

    val backgroundColor by animateColorAsState(
        targetValue =
        if (isActivePlayer())
            MaterialTheme.colors.secondary
        else
            Color.Unspecified,
        animationSpec = tween(durationMillis = 750)
    )

    val borderColor by animateColorAsState(
        targetValue =
        if (isActivePlayer())
            MaterialTheme.colors.onSurface.copy(0.9f)
        else
            Color.Unspecified,
        animationSpec = tween(durationMillis = 750)
    )

    Card(
        modifier = Modifier
            .padding(4.dp)
            .width(size)
            .border(
                width = 4.dp,
                borderColor,
                shape = RoundedCornerShape(4.dp)
            )
    ) {
        Surface(
            color = backgroundColor
        ) {
            Row(
                modifier = Modifier.width(200.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(0.8f)
                ) {
                    LaunchedEffect(viewModel.avatar.value) {
                        UserRepository.getUserByName(player.name) {
                            if (viewModel.avatar.value != it.avatar) {
                                viewModel.avatar.value = it.avatar
                            }
                        }
                    }

                    UserInfoView(player, viewModel.avatar.value)
                    Divider()
                    Text(
                        text = "${player.points} points",
                        style = MaterialTheme.typography.caption
                    )
                }
                Icon(
                    imageVector = Icons.Filled.Visibility,
                    contentDescription = null,
                    modifier = Modifier.alpha(if (isWatchedPlayer()) 1.0f else 0.0f)
                )
            }
        }
    }
}

@Composable
fun UserInfoView(player: Player, avatar: String = NoAvatar) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(
            modifier = Modifier.size(18.dp),
            avatarId = getAssetsId(avatar)
        )
        Spacer(modifier = Modifier.size(5.dp))
        Text(
            text = player.name,
            style = MaterialTheme.typography.h6
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun PlayersInfoPreview() {
    val gvm: GameViewModel = viewModel()
    gvm.game.players = mutableStateListOf(
        Player("ABC", 1),
        Player("DEF", 2),
        Player("GHI", 32432),
        Player("012345678901234567890123456789", 86867867),
    )
    gvm.game.activePlayerIndex.value = 2
    gvm.game.watchedPlayerIndex.value = 3
    PlayersInfoView(gvm)
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun DarkPlayersInfoPreview() {
    val gvm: GameViewModel = viewModel()
    gvm.game.players = mutableStateListOf(
        Player("ABC", 1),
        Player("DEF", 2),
        Player("GHI", 32432),
        Player("012345678901234567890123456789", 86867867),
    )
    gvm.game.activePlayerIndex.value = 2
    gvm.game.watchedPlayerIndex.value = 3
    PolyScrabbleClientTheme(isDarkTheme = mutableStateOf(true)) {
        PlayersInfoView(gvm)
    }
}
