package com.example.polyscrabbleclient.lobby.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.polyscrabbleclient.lobby.sources.*
import com.example.polyscrabbleclient.ui.theme.joinGameButtonFR

@Composable
fun PendingGamesView(
    pendingGames: MutableState<PendingGames?>,
    joinGame: (pendingGameIndex: Int) -> Unit
) {
    val selectedPendingGameIndex = remember {
        mutableStateOf<Int?>(null)
    }

    val columnsWeights = listOf(0.1f, 0.2f, 0.2f, 0.2f, 0.1f, 0.2f)
    val columnsHeaders = listOf("ID", "Mode", "Nb Joueurs", "Temps par tour", "Bonus", "Difficulté")

    Column {
        Card(
            modifier = Modifier
                .fillMaxSize(0.7f)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    Row {
                        columnsHeaders.forEachIndexed { index, header ->
                            TableCell(text = header, weight = columnsWeights[index])
                        }
                    }
                }
                pendingGames.value?.let {
                    items(it.size) { index ->

                        fun isPendingGameSelected(index: Int): Boolean {
                            return selectedPendingGameIndex.value == index
                        }

                        fun toggleSelected(index: Int) {
                            if (isPendingGameSelected(index)) {
                                selectedPendingGameIndex.value = null
                            } else {
                                selectedPendingGameIndex.value = index
                            }
                        }

                        PendingGameView(
                            pendingGameModel = pendingGames.value!![index],
                            columnsWeights,
                            click = { toggleSelected(index) }
                        ) { isPendingGameSelected(index) }
                    }
                }
            }

        }

        fun canJoinGame(): Boolean {
            return selectedPendingGameIndex.value !== null
        }

        Button(
            onClick = { joinGame(selectedPendingGameIndex.value!!) },
            enabled = canJoinGame()
        ) {
            Text(joinGameButtonFR)
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun PendingGamesPreview() {
    val a = mutableStateOf<PendingGames?>(
        listOf(
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
                hasPassword = false,
                password = "",
                privateGame = false,
            )
        ).toTypedArray()
    )
    PendingGamesView(
        a
    ) {}
}
