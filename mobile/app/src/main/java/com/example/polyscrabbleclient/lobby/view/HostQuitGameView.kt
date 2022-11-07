package com.example.polyscrabbleclient.lobby.view

import com.example.polyscrabbleclient.ui.theme.Ok
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.lobby.domain.ModalActions

@Composable
fun HostQuitGameView (
    modalButtons: @Composable (
        modalActions: ModalActions
    ) -> Unit
) {
    modalButtons(
        ModalActions(
            cancel = ActionButton(
                label = { Ok }
            )
        )
    )
}


@Preview(showBackground = true)
@Composable
fun HostQuitGamePreview() {
    HostQuitGameView {}
}

