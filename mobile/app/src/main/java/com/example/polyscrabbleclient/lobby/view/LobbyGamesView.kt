package com.example.polyscrabbleclient.lobby.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.lobby.domain.ModalActions
import com.example.polyscrabbleclient.lobby.sources.BotDifficulty
import com.example.polyscrabbleclient.lobby.sources.GameMode
import com.example.polyscrabbleclient.lobby.sources.LobbyGamesList
import com.example.polyscrabbleclient.lobby.sources.OnlineGameSettings
import com.example.polyscrabbleclient.lobby.viewmodels.JoinGameViewModel
import com.example.polyscrabbleclient.lobby.viewmodels.LobbyGameType
import com.example.polyscrabbleclient.ui.theme.joinGameButtonFR

val ColumnsWeights = listOf(
    0.3f,
    0.1f,
    0.1f,
    0.1f,
    0.1f,
    0.2f,
    0.1f
)


@Composable
fun LobbyGamesView(
    lobbyGames: MutableState<LobbyGamesList?>,
    lobbyGameType: LobbyGameType,
    viewModel: JoinGameViewModel = viewModel(),
    modalButtons: @Composable (modalActions: ModalActions) -> Unit
) {
    Column {
        lobbyGames.value?.filter { onlineGameSettings ->
            onlineGameSettings.gameMode == viewModel.getSelectedGameMode()
        }.let { lobbyGames ->
            LobbyGamesTableView(
                lobbyGames,
                lobbyGameType,
                { viewModel.toggleSelectedGame(it) },
                { viewModel.isGameSelected(it) },
            )
        }

        modalButtons(
            ModalActions(
                primary = ActionButton(
                    label = { joinGameButtonFR },
                    canAction = { viewModel.selectedLobbyGame.value !== null },
                )
            )
        )
    }
}

@Composable
private fun LobbyGamesTableView(
    games: List<OnlineGameSettings>?,
    lobbyGameType: LobbyGameType,
    toggleSelected: (lobbyGame: OnlineGameSettings) -> Unit,
    isGameSelected: (lobbyGame: OnlineGameSettings) -> Boolean,
) {
    Card(
        modifier = Modifier
            .height(250.dp)
            .padding(16.dp)
    ) {
        Column {
            LobbyGamesListView(
                games,
                lobbyGameType,
                { toggleSelected(it) },
                { isGameSelected(it) },
            )
        }
    }
}

@Composable
private fun LobbyGamesListView(
    lobbyGamesList: List<OnlineGameSettings>?,
    lobbyGameType: LobbyGameType,
    toggleSelected: (lobbyGame: OnlineGameSettings) -> Unit,
    isGameSelected: (lobbyGame: OnlineGameSettings) -> Boolean,
) {
    val primary = MaterialTheme.colors.primary
    val columnsHeader = formatHeaderPlayerCounts(lobbyGameType)
    Column {
        Row(
            modifier = Modifier.drawBehind {
                val borderSize = 4.dp.toPx()
                drawLine(
                    color = primary.copy(alpha = 0.2f),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = borderSize
                )
            }
        ) {
            columnsHeader.forEachIndexed { index, header ->
                HeaderTableCell(text = header, weight = ColumnsWeights[index])
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            lobbyGamesList?.let { lobbyGames ->
                items(lobbyGames.size) { index ->
                    val lobbyGameModel = lobbyGames[index]
                    LobbyGameView(
                        lobbyGameModel,
                        click = { toggleSelected(lobbyGameModel) }
                    ) { isGameSelected(lobbyGameModel) }
                }
            }
        }
    }
}

fun formatHeaderPlayerCounts(gameStatus: LobbyGameType): List<String> {
    return listOf(
        "Joueurs",
        "Bonus aléatoire",
        "Temps par tour",
        "Type de partie ",
        "Visibilité",
        if (gameStatus === LobbyGameType.Pending) "Joueur:IA/Max" else "Joueur:IA/Max|Obsrv",
        "Difficulté IA"
    )
}


@SuppressLint("UnrememberedMutableState", "MutableCollectionMutableState")
@Preview(showBackground = true, device = Devices.AUTOMOTIVE_1024p)
@Composable
fun LobbyPendingGamesPreview() {
    val a = OnlineGameSettings(
        id = "123",
        gameMode = GameMode.Classic,
        botDifficulty = BotDifficulty.Easy,
        numberOfPlayers = 4,
        playerNames = ArrayList(listOf("A", "B", "C")),
        randomBonus = false,
        timePerTurn = 60000,
        tmpPlayerNames = ArrayList(),
        gameStatus = "En attente",
        password = "",
        privateGame = false,
        drawableMagicCards = ArrayList(listOf()),
    )
    LobbyGamesView(
        mutableStateOf(
            ArrayList(listOf(a)),
        ),
        LobbyGameType.Pending,
        JoinGameViewModel(),
    ) {}
}

@SuppressLint("UnrememberedMutableState", "MutableCollectionMutableState")
@Preview(showBackground = true, device = Devices.AUTOMOTIVE_1024p)
@Composable
fun LobbyObservableGamesPreview() {
    val b = OnlineGameSettings(
        id = "123",
        gameMode = GameMode.Classic,
        botDifficulty = BotDifficulty.Easy,
        numberOfPlayers = 4,
        playerNames = ArrayList(listOf("D", "E", "F")),
        randomBonus = false,
        timePerTurn = 60000,
        tmpPlayerNames = ArrayList(),
        gameStatus = "En attente",
        password = "",
        privateGame = false,
        drawableMagicCards = ArrayList(listOf()),
    )
    LobbyGamesView(
        mutableStateOf(
            ArrayList(listOf(b)),
        ),
        LobbyGameType.Pending,
        JoinGameViewModel(),
    ) {}
}
