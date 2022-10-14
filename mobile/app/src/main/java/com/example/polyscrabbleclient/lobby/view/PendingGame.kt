package com.example.polyscrabbleclient.lobby.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.polyscrabbleclient.lobby.sources.BotDifficulty
import com.example.polyscrabbleclient.lobby.sources.GameMode
import com.example.polyscrabbleclient.lobby.sources.OnlineGameSettingsUI

@Composable
fun PendingGameView(
    pendingGameModel: OnlineGameSettingsUI,
    click: () -> Unit,
    isSelected: () -> Boolean
) {
    val targetColor by animateColorAsState(
        targetValue =
        if (isSelected())
            MaterialTheme.colors.secondary
        else
            MaterialTheme.colors.background,
        animationSpec = tween(durationMillis = 200)
    )

    Row (
        Modifier
            .background(targetColor)
            .padding(3.dp)
            .clickable { click() },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = pendingGameModel.id)
        Text(text = pendingGameModel.gameMode.value)
        Text(text = pendingGameModel.numberOfPlayers.toString())
        Text(text = pendingGameModel.botDifficulty.value)
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun PendingGamePreview() {
    PendingGameView(
        OnlineGameSettingsUI (
            id = "123",
            gameMode = GameMode.Classic,
            botDifficulty = BotDifficulty.Easy,
            numberOfPlayers = 2,
            playerNames = ArrayList(listOf("A", "B")),
            randomBonus = false,
            timePerTurn = 60
        ),
        {},
        { true }
    )
}

@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun PendingGamePreview2() {
    PendingGameView(
        OnlineGameSettingsUI (
            id = "123",
            gameMode = GameMode.Classic,
            botDifficulty = BotDifficulty.Easy,
            numberOfPlayers = 2,
            playerNames = ArrayList(listOf("A", "B")),
            randomBonus = false,
            timePerTurn = 60
        ),
        {},
        { false }
    )
}
