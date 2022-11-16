package com.example.polyscrabbleclient.lobby.domain

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.polyscrabbleclient.ui.theme.cancelButtonFR

data class ActionButton(
    val label: () -> String = { cancelButtonFR },
    val canAction: () -> Boolean = { true },
    val action: () -> Unit = { },
    val icon: ImageVector? = null,
)

data class ModalActions(
    val primary: ActionButton? = null,
    val cancel: ActionButton = ActionButton()
)

enum class ModalResult {
    Primary,
    Cancel
}

