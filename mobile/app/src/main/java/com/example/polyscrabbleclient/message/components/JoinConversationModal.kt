package com.example.polyscrabbleclient.message.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Input
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.polyscrabbleclient.lobby.domain.ActionButton
import com.example.polyscrabbleclient.lobby.domain.ModalActions
import com.example.polyscrabbleclient.lobby.view.ModalView
import com.example.polyscrabbleclient.message.model.Conversation
import com.example.polyscrabbleclient.message.viewModel.JoinConversationViewModel
import com.example.polyscrabbleclient.ui.theme.join_convo_title

@Composable
fun JoinConversationModal(
    opened: Boolean,
    onClose: () -> Unit,
) {
    if (!opened) {
        return
    }

    ModalView(
        closeModal = {},
        title = join_convo_title
    ) { modalButtons ->
        JoinConversationModalContent(onClose = onClose) { modalActions ->
            modalButtons(modalActions)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun JoinConversationModalContent(
    onClose: () -> Unit,
    modalButtons: @Composable (
        modalActions: ModalActions
    ) -> Unit
) {
    val viewModel: JoinConversationViewModel = viewModel()

    val conversations: LazyPagingItems<Conversation> = viewModel.conversationsPager.collectAsLazyPagingItems()
    var keyboardController = LocalSoftwareKeyboardController.current
    val inputFocusRequester = LocalFocusManager.current
    Column(
        Modifier
            .width(500.dp)
            .height(400.dp)
            .clickable { keyboardController?.hide(); inputFocusRequester.clearFocus() }
    ){
        SearchBar(
            conversationName = viewModel.conversationName,
            onSearch = {
                conversations.refresh()
            },
        )
        ConversationList(
            conversations,
            onJoin = { conversation ->
                viewModel.joinConversation(conversation) {
                    onClose()
                    viewModel.cancel()
                    conversations.refresh()
                }
            },
            isConversationJoinable = { conversation ->  viewModel.isConversationJoinable(conversation) },
        )
    }

    modalButtons(
        ModalActions(
            cancel = ActionButton(
                action = {
                    onClose()
                    viewModel.cancel()
                    conversations.refresh()
                }
            ),
        ),
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    conversationName: MutableState<String>,
    onSearch: (String) -> Unit,
) {

    var convoName by remember {
        conversationName
    }

    var keyboardController = LocalSoftwareKeyboardController.current

    Row(Modifier.padding(bottom = 20.dp)) {
        TextField(
            value = convoName,
            onValueChange = { convoName = it },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch(convoName)
                    keyboardController?.hide()
                }
            )
        )
        Button(
            onClick = { onSearch(convoName); keyboardController?.hide() }
        ) {
            Icon(Icons.Default.Search, null)
        }
    }
}

@Composable
fun ConversationList(
    conversations: LazyPagingItems<Conversation>,
    onJoin: (Conversation) -> Unit,
    isConversationJoinable: (Conversation) -> Boolean,
) {
    LazyColumn(
        Modifier
            .fillMaxWidth()
            .height(500.dp)
            .padding(horizontal = 30.dp)) {
        items(conversations) { conversation ->
            conversation?.let {
                JoinableConversation(
                    conversation = conversation,
                    onJoin = { onJoin(conversation) },
                    joined = { isConversationJoinable(conversation) }
                )
                Divider(Modifier.fillMaxWidth(), thickness = 1.dp)
            }
        }
    }
}

@Composable
fun JoinableConversation(
    conversation: Conversation,
    onJoin: () -> Unit,
    joined: () -> Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = conversation.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(0.7f)
        )
        Button(
            onClick = { onJoin() },
            enabled = joined()
        ) {
            Icon(Icons.Default.Input, null)
        }
    }
}
