package com.example.polyscrabbleclient.lobby.view

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.example.polyscrabbleclient.lobby.sources.BotDifficulty
import com.example.polyscrabbleclient.lobby.sources.GameMode
import com.example.polyscrabbleclient.lobby.sources.OnlineGameSettingsUI

@Composable
fun PendingGameView(
    pendingGameModel: OnlineGameSettingsUI
) {
    Box {
        Text(text = pendingGameModel.id)
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
        )
    )
}
