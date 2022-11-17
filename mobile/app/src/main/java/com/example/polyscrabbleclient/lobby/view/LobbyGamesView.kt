package com.example.polyscrabbleclient.lobby.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.lobby.domain.ModalActions
import com.example.polyscrabbleclient.lobby.sources.*
import com.example.polyscrabbleclient.ui.theme.ObservableGameSubTitle
import com.example.polyscrabbleclient.ui.theme.PendingGameSubTitle
import com.example.polyscrabbleclient.ui.theme.joinGameButtonFR
import com.example.polyscrabbleclient.utils.SubTitleView

val ColumnsWeights = listOf(
    0.3f,
    0.1f,
    0.1f,
    0.1f,
    0.1f,
    0.2f,
    0.1f
)
val ColumnsHeaders = listOf(
    "Joueurs",
    "Bonus aléatoire",
    "Temps par tour",
    "Mot de passe ?",
    "Type de partie ",
    "Joueur:IA/Max|Obsrv",
    "Difficulté IA"
)

@Composable
fun LobbyGamesView(
    lobbyGames: MutableState<LobbyGames?>,
    gameMode: GameMode,
    joinGame: (lobbyGameId: LobbyGameId) -> Unit,
    modalButtons: @Composable (modalActions: ModalActions) -> Unit
) {
    var selectedGameIndex by remember {
        mutableStateOf<LobbyGameId?>(null)
    }

    fun isGameSelected(lobbyGameId: LobbyGameId): Boolean {
        return selectedGameIndex == lobbyGameId
    }

    fun toggleSelectedGame(lobbyGameId: LobbyGameId) {
        selectedGameIndex =
            if (isGameSelected(lobbyGameId)) {
                null
            } else {
                lobbyGameId
            }
    }

    Column {
        lobbyGames.value?.pendingGamesSettings?.filter { onlineGameSettings ->
            onlineGameSettings.gameMode == gameMode
        }.let { pendingGames ->
            LobbyGamesTableView(
                PendingGameSubTitle,
                pendingGames,
                { toggleSelectedGame(it) },
                { isGameSelected(it) },
            )
        }

        lobbyGames.value?.observableGamesSettings?.filter { onlineGameSettings ->
            onlineGameSettings.gameMode == gameMode
        }.let { observableGames ->
            LobbyGamesTableView(
                ObservableGameSubTitle,
                observableGames,
                { toggleSelectedGame(it) },
                { isGameSelected(it) },
            )
        }

        modalButtons(
            ModalActions(
                primary = ActionButton(
                    label = { joinGameButtonFR },
                    canAction = { selectedGameIndex !== null },
                    action = { joinGame(selectedGameIndex!!) }
                )
            )
        )
    }
}

@Composable
private fun LobbyGamesTableView(
    subTitle: String,
    games: List<OnlineGameSettings>?,
    toggleSelected: (lobbyGameId: String) -> Unit,
    isGameSelected: (lobbyGameId: String) -> Boolean,
) {
    Card(
        modifier = Modifier
            .height(250.dp)
            .padding(16.dp)
    ) {
        Column {
            SubTitleView(
                subTitle,
                Modifier.padding(12.dp)
            )
            LobbyGamesListView(
                games,
                { toggleSelected(it) },
                { isGameSelected(it) },
            )
        }
    }
}

@Composable
private fun LobbyGamesListView(
    lobbyGamesList: List<OnlineGameSettings>?,
    toggleSelected: (lobbyGameId: String) -> Unit,
    isGameSelected: (lobbyGameId: String) -> Boolean,
) {
    val primary = MaterialTheme.colors.primary
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
            ColumnsHeaders.forEachIndexed { index, header ->
                HeaderTableCell(text = header, weight = ColumnsWeights[index])
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            lobbyGamesList?.let {
                items(it.size) { index ->
                    val lobbyGameModel = lobbyGamesList[index]
                    LobbyGameView(
                        lobbyGameSettings = lobbyGamesList[index],
                        click = { toggleSelected(lobbyGameModel.id) }
                    ) { isGameSelected(lobbyGameModel.id) }
                }
            }
        }
    }


}

@SuppressLint("UnrememberedMutableState", "MutableCollectionMutableState")
@Preview(showBackground = true, device = Devices.AUTOMOTIVE_1024p)
@Composable
fun LobbyGamesPreview() {
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
            LobbyGames(
                ArrayList(listOf(a)),
                ArrayList(listOf(b)),
            )
        ),
        GameMode.Classic,
        {}
    ) {}
}
