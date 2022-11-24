package com.example.polyscrabbleclient.lobby.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.polyscrabbleclient.account.components.Avatar
import com.example.polyscrabbleclient.getAssetsId
import com.example.polyscrabbleclient.lobby.sources.OnlineGameSettings
import com.example.polyscrabbleclient.ui.theme.*
import com.example.polyscrabbleclient.utils.TextView

@Composable
fun LobbyGameView(
    lobbyGameSettings: OnlineGameSettings,
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
            formatPlayerNames(lobbyGameSettings.playerNames),
            formatRandomBonus(lobbyGameSettings.randomBonus),
            formatTurnTime(lobbyGameSettings.timePerTurn.toLong()),
            formatHasPassword(lobbyGameSettings.password),
            formatIsPrivate(lobbyGameSettings.privateGame),
            formatPlayersCounts(lobbyGameSettings),
            lobbyGameSettings.botDifficulty.value
        )
        fields.forEachIndexed { index, field ->
            TableCell(text = field, weight = ColumnsWeights[index])
        }
    }
}

fun formatPlayersCounts(lobbyGameSettings: OnlineGameSettings): String {
    val realPlayers = lobbyGameSettings.playerNames.size
    val max = lobbyGameSettings.numberOfPlayers
    val observers =
        if (lobbyGameSettings.observerNames?.isNotEmpty() == true)
            lobbyGameSettings.observerNames.size
        else
            0
    if (lobbyGameSettings.gameStatus == WAIT_STATUS) {
        val estimatedBots = lobbyGameSettings.numberOfPlayers - lobbyGameSettings.playerNames.size
        return "$realPlayers : $estimatedBots / $max | 0"
    }
    if (lobbyGameSettings.gameStatus == ACTIVE_STATUS) {
        val bots = lobbyGameSettings.numberOfBots
        val adjustedRealPlayers = if (bots !== null) realPlayers - bots else realPlayers
        return "$adjustedRealPlayers : $bots / $max | $observers"
    }
    return ""
}

fun formatIsPrivate(privateGame: Boolean): String {
    return if (privateGame) Private else Public
}

fun formatHasPassword(password: String?): String {
    return if (password === null || password.isEmpty()) No else Yes
}

fun formatRandomBonus(randomBonus: Boolean): String {
    return if (randomBonus) Activated else Deactivated
}

fun formatPlayerNames(playerNames: ArrayList<String>): String {
    return playerNames.reduce { acc, current -> "$acc, $current" }
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float
) {
    TextView(
        text = text,
        modifier = Modifier
            .weight(weight)
            .padding(8.dp)
    )
}

@Composable
fun RowScope.HeaderTableCell(
    text: String,
    weight: Float
) {
    TextView(
        text = text,
        isBold = true,
        modifier = Modifier
            .weight(weight)
            .padding(4.dp)
    )
}
