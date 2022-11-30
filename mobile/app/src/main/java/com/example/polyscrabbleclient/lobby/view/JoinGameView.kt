package com.example.polyscrabbleclient.lobby.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.polyscrabbleclient.lobby.domain.ModalActions
import com.example.polyscrabbleclient.lobby.sources.LobbyGamesList
import com.example.polyscrabbleclient.lobby.viewmodels.JoinGameViewModel
import com.example.polyscrabbleclient.lobby.viewmodels.LobbyGameType

@Composable
fun JoinGameView(
    lobbyGames: MutableState<LobbyGamesList?>,
    viewModel: JoinGameViewModel = viewModel(),
    lobbyGameType: LobbyGameType,
    modalButtons: @Composable (modalActions: ModalActions) -> Unit
) {
    EvenlySpacedRowContainer {
        Box {
            LobbyGamesView(
                lobbyGames,
                lobbyGameType,
                viewModel,
            ) { modalActions ->
                modalButtons(modalActions)
            }
        }
    }
}


@Composable
fun EvenlySpacedRowContainer(content: @Composable RowScope.() -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        content()
    }
}

@SuppressLint("UnrememberedMutableState", "MutableCollectionMutableState")
@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun JoinGamePreview() {
    JoinGameView(
        mutableStateOf(arrayListOf()),
        lobbyGameType = LobbyGameType.Pending,
    ) {}
}
