package com.example.polyscrabbleclient.lobby.view.createGame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.polyscrabbleclient.lobby.view.NewGameForm
import com.example.polyscrabbleclient.lobby.viewmodels.CreateGameViewModel
import com.example.polyscrabbleclient.ui.theme.cancel
import com.example.polyscrabbleclient.ui.theme.create_game
import com.example.polyscrabbleclient.ui.theme.new_game_creation

@Composable
fun CreateGameModalContent(
    updateState: (value: Boolean) -> Unit,
    createGameViewModel: CreateGameViewModel
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
            Text(
                modifier = Modifier.padding(vertical = 5.dp),
                text = new_game_creation,
                fontStyle = MaterialTheme.typography.h1.fontStyle,
                fontSize = 25.sp
            )
            NewGameForm(createGameViewModel)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 15.dp, 0.dp, 0.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { updateState(false) }) {
                    Text(text = cancel)
                }
                Button(onClick = { updateState(false); createGameViewModel.sendCreateGameRequest() }) {
                    Text(text = create_game)
                }
            }
        }
    }
}
