package com.example.polyscrabbleclient.game.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.polyscrabbleclient.game.sources.Player
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel

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
            .defaultMinSize(100.dp)
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

@Preview(showBackground = true)
@Composable
fun PlayersInfoPreview() {
    PlayersInfoView(GameViewModel())
}
