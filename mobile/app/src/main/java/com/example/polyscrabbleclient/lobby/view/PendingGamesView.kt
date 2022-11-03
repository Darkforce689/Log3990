package com.example.polyscrabbleclient.lobby.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.polyscrabbleclient.lobby.domain.ModalAction
import com.example.polyscrabbleclient.lobby.domain.ModalActions
import com.example.polyscrabbleclient.lobby.sources.*
import com.example.polyscrabbleclient.ui.theme.joinGameButtonFR

@Composable
fun PendingGamesView(
    pendingGames: MutableState<PendingGames?>,
    joinGame: (pendingGameIndex: Int) -> Unit,
    modalButtons: @Composable (
        modalActions: ModalActions
    ) -> Unit
) {
    var selectedPendingGameIndex by remember {
        mutableStateOf<Int?>(null)
    }

    val columnsWeights = listOf(0.1f, 0.2f, 0.2f, 0.2f, 0.1f, 0.2f)
    val columnsHeaders = listOf("ID", "Mode", "Nb Joueurs", "Temps par tour", "Bonus", "Difficulté")

    fun isPendingGameSelected(index: Int): Boolean {
        return selectedPendingGameIndex == index
    }

    fun toggleSelected(index: Int) {
        selectedPendingGameIndex =
            if (isPendingGameSelected(index)) {
                null
            } else {
                index
            }
    }

    Column {
        Card(
            modifier = Modifier
                .fillMaxHeight(0.6f)
                .padding(16.dp)
        ) {
            PendingGamesListView(
                columnsHeaders,
                columnsWeights,
                pendingGames,
                { toggleSelected(it) },
                { isPendingGameSelected(it) },
            )
        }

        modalButtons(
            ModalActions(
                primary = ModalAction(
                    label = joinGameButtonFR,
                    canAction = { selectedPendingGameIndex !== null },
                    action = { joinGame(selectedPendingGameIndex!!) }
                )
            )
        )
    }
}

@Composable
private fun PendingGamesListView(
    columnsHeaders: List<String>,
    columnsWeights: List<Float>,
    pendingGames: MutableState<PendingGames?>,
    toggleSelected: (index: Int) -> Unit,
    isPendingGameSelected: (index: Int) -> Boolean,
) {
    Column {
        Row {
            columnsHeaders.forEachIndexed { index, header ->
                HeaderTableCell(text = header, weight = columnsWeights[index])
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            pendingGames.value?.let {
                items(it.size) { index ->
                    PendingGameView(
                        pendingGameModel = pendingGames.value!![index],
                        columnsWeights,
                        click = { toggleSelected(index) }
                    ) { isPendingGameSelected(index) }
                }
            }
        }
    }


}

@SuppressLint("UnrememberedMutableState", "MutableCollectionMutableState")
@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun PendingGamesPreview() {
    val b = OnlineGameSettings(
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
        drawableMagicCards = ArrayList(listOf()),
    )
    PendingGamesView(
        mutableStateOf(ArrayList(listOf(b)))
    , {}) {}
}
