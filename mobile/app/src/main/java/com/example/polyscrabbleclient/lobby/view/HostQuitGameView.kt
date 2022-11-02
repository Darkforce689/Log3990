package com.example.polyscrabbleclient.lobby.view

import com.example.polyscrabbleclient.ui.theme.Ok
import com.example.polyscrabbleclient.ui.theme.hostQuitGameFR
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.polyscrabbleclient.lobby.domain.ModalAction
import com.example.polyscrabbleclient.lobby.domain.ModalActions

@Composable
fun HostQuitGameView (
    modalButtons: @Composable (
        modalActions: ModalActions
    ) -> Unit
) {
    modalButtons(
        ModalActions(
            cancel = ModalAction(
                label = Ok
            )
        )
    )
}


@Preview(showBackground = true)
@Composable
fun HostQuitGamePreview() {
    HostQuitGameView {}
}

