package com.example.polyscrabbleclient.lobby.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.polyscrabbleclient.account.components.Avatar
import com.example.polyscrabbleclient.getAssetsId
import com.example.polyscrabbleclient.lobby.sources.OnlineGameSettings
import com.example.polyscrabbleclient.ui.theme.*
import com.example.polyscrabbleclient.user.UserRepository
import com.example.polyscrabbleclient.utils.TextView
import kotlin.math.floor

@Composable
fun LobbyGameView(
    lobbyGameSettings: OnlineGameSettings,
    click: () -> Unit,
    isSelected: () -> Boolean
) {
    val players: MutableState<List<Pair<String, String>>> = remember { mutableStateOf(listOf()) }
    GetAvatars(lobbyGameSettings = lobbyGameSettings, players)

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
            formatRandomBonus(lobbyGameSettings.randomBonus),
            formatTurnTime(lobbyGameSettings.timePerTurn.toLong()),
            formatHasPassword(lobbyGameSettings.password),
            formatIsPrivate(lobbyGameSettings.privateGame),
            formatPlayersCounts(lobbyGameSettings),
        )
        PlayersCell(
            players = players.value,
            hostName = if (lobbyGameSettings.playerNames.isNotEmpty()) lobbyGameSettings.playerNames[0] else "",
            weight = ColumnsWeights[0]
        )
        fields.forEachIndexed { index, field ->
            TableCell(text = field, weight = ColumnsWeights[index + 1])
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
        return "$realPlayers : $estimatedBots / $max"
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
    return if (password === null || password.isEmpty()) NotProtected else Protected
}

fun formatRandomBonus(randomBonus: Boolean): String {
    return if (randomBonus) Activated else Deactivated
}

@Composable
fun GetAvatars(
    lobbyGameSettings: OnlineGameSettings,
    players: MutableState<List<Pair<String, String>>>
) {
    LaunchedEffect(key1 = lobbyGameSettings) {
        players.value = listOf()
        var playerNames = lobbyGameSettings.playerNames
        val nPlayers = lobbyGameSettings.playerNames.size
        val totalPlayers = lobbyGameSettings.numberOfPlayers
        val botNames = lobbyGameSettings.botNames?.slice(nPlayers - 1 until totalPlayers - 1)
        if (!botNames.isNullOrEmpty()) {
            playerNames = (lobbyGameSettings.playerNames + botNames) as ArrayList<String>
        }
        for (playerName in playerNames) {
            UserRepository.getUserByName(playerName) {
                players.value = players.value.plus(Pair(it.name, it.avatar))
            }
        }
    }
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
fun RowScope.PlayersCell(
    players: List<Pair<String, String>>,
    hostName: String,
    weight: Float,
) {
    val first = players.slice(0 until floor((players.size / 2).toDouble()).toInt())
    val last = players.slice(floor((players.size / 2).toDouble()).toInt() until players.size)
    val listPlayers = listOf(first, last)
    Row(
        modifier = Modifier
            .weight(weight),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listPlayers.forEach { players ->
            Column(Modifier.wrapContentWidth()) {
                players.forEach {
                    val isHost = it.first == hostName
                    PlayerAndAvatar(isHost, it)
                }
            }
        }
    }
}

@Composable
fun PlayerAndAvatar(
    isHost: Boolean = false,
    playerInfo: Pair<String, String>,
) {
    Row {
        Avatar(
            modifier = Modifier
                .size(30.dp)
                .padding(2.dp),
            avatarId = getAssetsId(playerInfo.second)
        )
        TextView(
            text = playerInfo.first,
            isBold = isHost
        )
    }
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

@Preview
@Composable
fun PlayersCellPreview() {
    val list = listOf(
        Pair("juju9", "stag"),
        Pair("juju8", "polarbear"),
        Pair("Juan", "hardBotAvatar1"),
        Pair("Noah", "hardBotAvatar2")
    )
    Row {
        PlayersCell(list, list[0].first, 0.5f)
    }
}
