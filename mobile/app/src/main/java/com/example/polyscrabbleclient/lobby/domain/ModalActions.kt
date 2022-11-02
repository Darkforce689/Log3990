package com.example.polyscrabbleclient.lobby.domain

import com.example.polyscrabbleclient.ui.theme.cancelButtonFR

data class ModalAction (
    val label: String = cancelButtonFR,
    val canAction: () -> Boolean = { true },
    val action: () -> Unit = { },
)

data class ModalActions (
    val primary: ModalAction? = null,
    val cancel: ModalAction = ModalAction()
)

enum class ModalResult {
    Primary,
    Cancel
}

