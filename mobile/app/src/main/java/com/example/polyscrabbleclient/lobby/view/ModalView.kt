package com.example.polyscrabbleclient.lobby.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.lobby.domain.ModalActions
import com.example.polyscrabbleclient.lobby.domain.ModalResult
import com.example.polyscrabbleclient.ui.theme.PolyScrabbleClientTheme
import com.example.polyscrabbleclient.ui.theme.SpinnerView
import com.example.polyscrabbleclient.ui.theme.joinGameButtonFR


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ModalView(
    closeModal: (modalResult: ModalResult) -> Unit,
    title: String,
    hasSpinner: Boolean = false,
    minWidth: Dp = 400.dp,
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
                Surface {
                    Column(
                        modifier = Modifier
                            .defaultMinSize(minWidth)
                            .padding(18.dp)
                    ) {
                        Row(
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
                            Row(modifier = Modifier.padding(top = 18.dp)) {
                                ModalButton(
                                    { closeModal(ModalResult.Cancel) },
                                    modalActions.cancel
                                )
                                modalActions.primary?.let {
                                    ModalButton(
                                        { closeModal(ModalResult.Primary) },
                                        it
                                    )
                                }
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
    actionButton: ActionButton
) {
    Button(
        modifier = Modifier.padding(end = 8.dp),
        onClick = {
            actionButton.action()
            closeModal()
        },
        enabled = actionButton.canAction()
    ) {
        Text(text = actionButton.label())
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun ModalPreview() {
    ModalView(
        closeModal = { },
        title = "This is a modal title",
    ) {
        it(
            ModalActions(
                primary = ActionButton(
                    label = { joinGameButtonFR },
                    canAction = { false },
                    action = {}
                )
            )
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun ModalWithSpinnerPreview() {
    ModalView(
        closeModal = { },
        title = "This is a modal title",
        hasSpinner = true
    ) {
        it(
            ModalActions(
                primary = ActionButton(
                    label = { joinGameButtonFR },
                    canAction = { false },
                    action = {}
                )
            )
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun DarkModalPreview() {
    PolyScrabbleClientTheme(isDarkTheme = mutableStateOf(true)) {
        ModalView(
            closeModal = { },
            title = "This is a modal title",
            hasSpinner = false
        ) {
            it(
                ModalActions(
                    primary = ActionButton(
                        label = { joinGameButtonFR },
                        canAction = { false },
                        action = {}
                    )
                )
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun DarkModalWithSpinnerPreview() {
    PolyScrabbleClientTheme(isDarkTheme = mutableStateOf(true)) {
        ModalView(
            closeModal = { },
            title = "This is a modal title",
            hasSpinner = true
        ) {
            it(
                ModalActions(
                    primary = ActionButton(
                        label = { joinGameButtonFR },
                        canAction = { false },
                        action = {}
                    )
                )
            )
        }
    }
}
