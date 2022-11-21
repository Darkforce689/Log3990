package com.example.polyscrabbleclient.message.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.lobby.domain.ModalActions
import com.example.polyscrabbleclient.lobby.view.ModalView
import com.example.polyscrabbleclient.message.viewModel.CreateConversationViewModel
import com.example.polyscrabbleclient.message.viewModel.ErrorState
import com.example.polyscrabbleclient.ui.theme.create_convo_button
import com.example.polyscrabbleclient.ui.theme.create_convo_title

@Composable()
fun CreateConversationModal(
    opened: Boolean,
    onClose: () -> Unit,
) {
    if (!opened) {
        return
    }

    ModalView(
        closeModal = {},
        title = create_convo_title,
    ) { modalButtons ->
        CreateConversationModalContent(onClose) { modalActions ->
            modalButtons(modalActions)
        }
    }
}

@Composable()
fun CreateConversationModalContent(
    onClose: () -> Unit,
    modalButtons: @Composable (
        modalActions: ModalActions
    ) -> Unit
) {
    val createConvoViewModel: CreateConversationViewModel = viewModel()
    Column(
        modifier = Modifier
            .padding(top = 20.dp)
            .width(300.dp),
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            value = createConvoViewModel.convoName.value,
            onValueChange = {
                createConvoViewModel.onNameChange(it)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (createConvoViewModel.canCreateConversation()) {
                        createConvoViewModel.createConversation() {
                            if (it == ErrorState.NotError) {
                                onClose()
                            }
                        }
                    }
                }
            )
        )

        if (createConvoViewModel.error.value !== null) {
            Text(
                text = createConvoViewModel.error.value!!,
                color = MaterialTheme.colors.error,
                modifier = Modifier.height(20.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    modalButtons(
        ModalActions(
            cancel = ActionButton(
                action = {
                    createConvoViewModel.cancel()
                    onClose()
                }
            ),
            primary = ActionButton(
                label = { create_convo_button },
                canAction = { createConvoViewModel.canCreateConversation() },
                action = {
                    createConvoViewModel.createConversation() {
                        if (it == ErrorState.NotError) {
                            onClose()
                        }
                    }
                }
            )
        ),
    )
}
