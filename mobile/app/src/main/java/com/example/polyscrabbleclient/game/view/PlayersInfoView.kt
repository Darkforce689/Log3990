package com.example.polyscrabbleclient.game.view

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.polyscrabbleclient.game.sources.Player
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel
import com.example.polyscrabbleclient.ui.theme.PolyScrabbleClientTheme

@Composable
fun PlayersInfoView(viewModel: GameViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        viewModel.game.players.value.forEach { player ->
            PlayerInfoView(player) {
                player === viewModel.game.getActivePlayer()
            }
        }
    }
}

@Composable
fun PlayerInfoView(
    player: Player,
    isActivePlayer: () -> Boolean
) {

    val targetColor by animateColorAsState(
        targetValue =
        if (isActivePlayer())
            MaterialTheme.colors.primary
        else
            MaterialTheme.colors.secondary,
        animationSpec = tween(durationMillis = 200)
    )

    Surface(
        modifier = Modifier
            .border(width = 4.dp, targetColor)
            .padding(10.dp)
            .width(180.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = player.name,
                style = MaterialTheme.typography.h6
            )
            Text(
                text = "${player.points} points",
                style = MaterialTheme.typography.caption
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun PlayersInfoPreview() {
    val gvm: GameViewModel = viewModel()
    gvm.game.players = mutableStateOf(
        listOf(
            Player("ABC", 1),
            Player("DEF", 2),
            Player("GHI", 32432),
            Player("012345678901234567890123456789", 86867867),
        )
    )
    gvm.game.activePlayerIndex.value = 2
    PlayersInfoView(gvm)
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun DarkPlayersInfoPreview() {
    val gvm: GameViewModel = viewModel()
    gvm.game.players = mutableStateOf(
        listOf(
            Player("ABC", 1),
            Player("DEF", 2),
            Player("GHI", 32432),
            Player("012345678901234567890123456789", 86867867),
        )
    )
    gvm.game.activePlayerIndex.value = 2
    PolyScrabbleClientTheme(isDarkTheme = mutableStateOf(true)) {
        PlayersInfoView(gvm)
    }
}
