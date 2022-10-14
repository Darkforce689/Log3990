package com.example.polyscrabbleclient.lobby.view

import android.annotation.SuppressLint
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.example.polyscrabbleclient.lobby.sources.BotDifficulty
import com.example.polyscrabbleclient.lobby.sources.GameMode
import com.example.polyscrabbleclient.lobby.sources.OnlineGameSettingsUI
import com.example.polyscrabbleclient.lobby.sources.PendingGames

@Composable
fun PendingGamesView(pendingGames: MutableState<PendingGames?>) {
    LazyColumn {
        pendingGames.value?.let {
            items(it.size) { index ->
                PendingGameView(pendingGameModel = pendingGames.value!![index])
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun PendingGamesPreview() {

    val a = mutableStateOf<PendingGames?>(
        listOf(
            OnlineGameSettingsUI (
                id = "123",
                gameMode = GameMode.Classic,
                botDifficulty = BotDifficulty.Easy,
                numberOfPlayers = 2,
                playerNames = ArrayList(listOf("A", "B")),
                randomBonus = false,
                timePerTurn = 60
            )
        ).toTypedArray()
    )
    PendingGamesView(a)
}
