package com.example.polyscrabbleclient.lobby.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.polyscrabbleclient.lobby.sources.OnlineGameSettings

@Composable
fun PendingGameView(
    pendingGameModel: OnlineGameSettings,
    columnsWeights: List<Float>,
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

    Row(
        Modifier
            .background(targetColor)
            .clickable { click() },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val fields = listOf(
            pendingGameModel.id,
            pendingGameModel.gameMode.value,
            pendingGameModel.numberOfPlayers.toString(),
            pendingGameModel.timePerTurn.toString(),
            if (pendingGameModel.randomBonus) "Oui" else "Non",
            pendingGameModel.botDifficulty.value
        )
        fields.forEachIndexed { index, field ->
            TableCell(text = field, weight = columnsWeights[index])
        }
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
    )
}

@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun PendingGamePreview() {
    PendingGameView(
        OnlineGameSettings(
            id = "123",
            gameMode = GameMode.Classic,
            botDifficulty = BotDifficulty.Easy,
            numberOfPlayers = 2,
            playerNames = ArrayList(listOf("A", "B")),
            tmpPlayerNames = ArrayList(),
            randomBonus = false,
            timePerTurn = 60,
            gameStatus = "En attente",
            password = "",
            privateGame = false,
        ),
        listOf(0.1f, 0.2f, 0.2f, 0.2f, 0.1f, 0.2f),
        {}
    ) { true }
}

@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun PendingGamePreview2() {
    PendingGameView(
        OnlineGameSettings(
            id = "123",
            gameMode = GameMode.Classic,
            botDifficulty = BotDifficulty.Easy,
            numberOfPlayers = 2,
            playerNames = ArrayList(listOf("A", "B")),
            randomBonus = false,
            timePerTurn = 60,
            tmpPlayerNames = ArrayList(),
            gameStatus = "En attente",
            password = "",
            privateGame = false,
        ),
        listOf(0.1f, 0.2f, 0.2f, 0.2f, 0.1f, 0.2f),
        {}
    ) { false }
}
