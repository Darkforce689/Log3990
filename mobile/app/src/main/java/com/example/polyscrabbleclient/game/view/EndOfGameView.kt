package com.example.polyscrabbleclient.game.view

import com.example.polyscrabbleclient.ui.theme.Ok
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.lobby.domain.ModalActions

@Composable
fun EndOfGameView (
    viewModel: GameViewModel = viewModel(),
    modalButtons: @Composable (
        modalActions: ModalActions
    ) -> Unit
) {
    Column {

        Text(
            modifier = Modifier.padding(vertical = 5.dp),
            text = viewModel.getFormattedWinners(),
        )

        modalButtons(
            ModalActions(
                cancel = ActionButton(
                    label = { Ok }
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EndOfGamePreview() {
    EndOfGameView {}
}

