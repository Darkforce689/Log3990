package com.example.polyscrabbleclient.lobby.view.createGame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.polyscrabbleclient.lobby.domain.ModalAction
import com.example.polyscrabbleclient.lobby.domain.ModalActions
import com.example.polyscrabbleclient.lobby.viewmodels.CreateGameViewModel
import com.example.polyscrabbleclient.ui.theme.create_game

@Composable
fun CreateGameModalContent(
    createGameViewModel: CreateGameViewModel,
    modalButtons: @Composable (
        modalActions: ModalActions
    ) -> Unit
) {
    Card(
        modifier = Modifier
            .width(400.dp)
            .background(Color.White),
    ) {
        Column(
            Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            NewGameForm(createGameViewModel)

        }
    }
    modalButtons(
        ModalActions(
            ModalAction(
                label = create_game,
                canAction = { true },
                action = { createGameViewModel.sendCreateGameRequest() }
            )
        )
    )
}
