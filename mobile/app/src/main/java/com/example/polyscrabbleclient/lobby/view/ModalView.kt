package com.example.polyscrabbleclient.lobby.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.polyscrabbleclient.lobby.domain.ModalAction
import com.example.polyscrabbleclient.lobby.domain.ModalActions
import com.example.polyscrabbleclient.lobby.domain.ModalResult
import com.example.polyscrabbleclient.ui.theme.SpinnerView
import com.example.polyscrabbleclient.ui.theme.joinGameButtonFR
import com.example.polyscrabbleclient.ui.theme.waitingForOtherPlayersFR


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ModalView(
    closeModal: (modalResult: ModalResult) -> Unit,
    title: String,
    hasSpinner: Boolean = false,
    width: Dp = 400.dp,
    content: @Composable (
        modalButtons: @Composable (
            modalActions: ModalActions
        ) -> Unit
    ) -> Unit
) {
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { },
        content = {
            Card {
                Column (
                    modifier = Modifier
                        .width(width)
                        .padding(18.dp)
                        .background(Color.White)
                ) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = title,
                            fontStyle = MaterialTheme.typography.h1.fontStyle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp
                        )
                        if (hasSpinner) {
                            SpinnerView()
                        }
                    }

                    content { modalActions ->
                        Row {
                            modalActions.cancel.let {
                                ModalButton(
                                    { closeModal(ModalResult.Cancel) },
                                    it.canAction,
                                    it.action,
                                    it.label
                                )
                            }
                            modalActions.primary?.let {
                                ModalButton(
                                    { closeModal(ModalResult.Primary) },
                                    it.canAction,
                                    it.action,
                                    it.label
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun ModalButton(
    closeModal: () -> Unit,
    canAction: () -> Boolean,
    action: () -> Unit,
    buttonLabel: String
) {
    Button(
        modifier = Modifier.padding(end = 8.dp),
        onClick = {
            action()
            closeModal()
        },
        enabled = canAction()
    ) {
        Text(text = buttonLabel)
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun SpinnerModalPreview() {
    ModalView(
        closeModal = { },
        title = "This is a modal title",
        hasSpinner = true
    ) {
        it(
            ModalActions(
                primary = ModalAction(
                    label = joinGameButtonFR,
                    canAction = { false },
                    action = {}
                )
            )
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun NoSpinnerModalPreview() {
    ModalView(
        closeModal = { },
        title = "This is a modal title",
        hasSpinner = false
    ) {
        it(
            ModalActions(
                primary = ModalAction(
                    label = joinGameButtonFR,
                    canAction = { false },
                    action = {}
                )
            )
        )
    }
}
