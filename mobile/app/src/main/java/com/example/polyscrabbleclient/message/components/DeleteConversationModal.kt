package com.example.polyscrabbleclient.message.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.lobby.domain.ModalActions
import com.example.polyscrabbleclient.lobby.view.ModalView
import com.example.polyscrabbleclient.message.model.Conversation
import com.example.polyscrabbleclient.message.viewModel.DeleteConversationViewModel
import com.example.polyscrabbleclient.ui.theme.delete_convo_title
import com.example.polyscrabbleclient.ui.theme.no_conversation_to_delete

@Composable
fun DeleteConversationViewModal(
    opened: Boolean,
    onClose: () -> Unit,
) {
    if (!opened) {
        return
    }

    ModalView(
        closeModal = {},
        title = delete_convo_title
    ) { modalButtons ->
        DeleteConversationModalContent(onClose = onClose) { modalActions ->
            modalButtons(modalActions)
        }
    }
}

@Composable
fun DeleteConversationModalContent(
    onClose: () -> Unit,
    modalButtons: @Composable (
        modalActions: ModalActions
    ) -> Unit
) {
    val viewModel: DeleteConversationViewModel = viewModel()
    val conversations = viewModel.conversations
    Box(
        Modifier
            .width(500.dp)
            .height(400.dp)
            .padding(top = 20.dp, end = 10.dp, start = 10.dp)
    ) {
        if (conversations.isNotEmpty()) {
            ConversationDeleteList(
                conversations = conversations,
                onDelete = { viewModel.deleteConversation(it) },
                isDeleting = viewModel.isConvoDeleting
            )
        } else {
            NoConversationToDelete()
        }
    }

    modalButtons(
        ModalActions(
            cancel = ActionButton(
                action = {
                    onClose()
                }
            ),
        ),
    )
}

@Composable
fun ConversationDeleteList(
    conversations: SnapshotStateList<Conversation>,
    onDelete: (Conversation) -> Unit,
    isDeleting: MutableState<Boolean>,
) {
    Column {
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
        ) {
            items(conversations) { conversation ->
                Row (
                    modifier = Modifier.padding(vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        conversation.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(0.85f)
                    )
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onDelete(conversation)
                        },
                        enabled = !isDeleting.value
                    ) {
                        Icon(Icons.Default.Delete, null)
                    }
                }
                Divider(Modifier.fillMaxWidth(), thickness = 1.dp)
            }
        }
    }
}

@Composable
fun NoConversationToDelete() {
    Text(no_conversation_to_delete)
    Divider(thickness = 1.dp)
}
