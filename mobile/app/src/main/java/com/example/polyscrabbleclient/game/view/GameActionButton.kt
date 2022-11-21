package com.example.polyscrabbleclient.game.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Science
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.polyscrabbleclient.lobby.domain.ActionButton

val GameActionButton: @Composable (
    width: Dp,
    actionButton: ActionButton,
) -> Unit = { width, actionButton ->
    GameActionButtonView(
        actionButton = actionButton,
        width = width,
    )
}

@Composable
fun GameActionButtonView(
    actionButton: ActionButton,
    width: Dp = 200.dp,
    fontSize: TextUnit = TextUnit.Unspecified,
) {
    Button(
        modifier = Modifier
            .size(
                width = width,
                height = 90.dp
            )
            .padding(
                vertical = 15.dp,
                horizontal = 15.dp
            ),
        onClick = actionButton.action,
        enabled = actionButton.canAction()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text(
                    fontSize = fontSize,
                    text = actionButton.label()
                )
            }
            if (actionButton.icon !== null) {
                Icon(
                    actionButton.icon,
                    contentDescription = "${actionButton.icon}"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameActionButtonPreview() {
    GameActionButton(
        150.dp,
        ActionButton(
            action = { },
            canAction = { true },
            icon = Icons.Filled.Science,
            label = { "Science" }
        )
    )
}

@Preview(showBackground = true)
@Composable
fun LongerGameActionButtonPreview() {
    GameActionButtonView(
        ActionButton(
            action = { },
            canAction = { true },
            icon = Icons.Filled.Science,
            label = { "Science but we need a longer quote : Rien ne se perd, rien ne se crée, tout se transforme " }
        )
    )
}
