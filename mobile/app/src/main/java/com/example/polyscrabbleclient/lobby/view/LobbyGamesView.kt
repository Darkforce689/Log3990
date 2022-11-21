package com.example.polyscrabbleclient.lobby.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
    lobbyGames: MutableState<LobbyGamesList?>,
    selectedGameIndex: MutableState<Int?>,
    gameMode: GameMode,
    joinGame: (LobbyGamesList?) -> Unit,
    modalButtons: @Composable (modalActions: ModalActions) -> Unit
) {
    fun isGameSelected(lobbyGameIndex: Int): Boolean {
        return selectedGameIndex.value == lobbyGameIndex
    }

    fun toggleSelectedGame(lobbyGameIndex: Int) {
        selectedGameIndex.value =
            if (isGameSelected(lobbyGameIndex)) {
                null
            } else {
                lobbyGameIndex
            }
    }

    Column {
        lobbyGames.value?.pendingGamesSettings.let { pendingGames ->
            LobbyGamesTableView(
                PendingGameSubTitle,
                pendingGames,
                { toggleSelectedGame(it) },
                { isGameSelected(it) },
            )
        }

        lobbyGames.value?.observableGamesSettings.let { observableGames ->
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
                    canAction = { selectedGameIndex.value !== null },
                    action = { joinGame(lobbyGames.value) }
                )
            )
        )
    }
}

@Composable
private fun LobbyGamesTableView(
    subTitle: String,
    games: List<OnlineGameSettings>?,
    toggleSelected: (lobbyGameIndex: Int) -> Unit,
    isGameSelected: (lobbyGameIndex: Int) -> Boolean,
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
    toggleSelected: (lobbyGameIndex: Int) -> Unit,
    isGameSelected: (lobbyGameIndex: Int) -> Boolean,
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
                itemsIndexed(it) { index, lobbyGameModel ->
                    LobbyGameView(
                        lobbyGameSettings = lobbyGamesList[index],
                        click = { toggleSelected(index) }
                    ) { isGameSelected(index) }
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
    LobbyGamesView(
        mutableStateOf(
            ArrayList(listOf(a)),
        ),
        mutableStateOf(0),
        GameMode.Classic,
        {}
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
            LobbyGames(
                ArrayList(listOf(a)),
                ArrayList(listOf(b)),
            )
        ),
        mutableStateOf(0),
        GameMode.Classic,
        {}
    )
}
