package com.example.polyscrabbleclient.message.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.polyscrabbleclient.message.model.Message
import com.example.polyscrabbleclient.message.model.MessageType
import com.example.polyscrabbleclient.message.viewModel.ChatBoxViewModel

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun ChatBox(chatBoxViewModel: ChatBoxViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val messages = chatBoxViewModel.messages
    val history = chatBoxViewModel.historyPager.collectAsLazyPagingItems();
    chatBoxViewModel.setCurrentConversation()
    val inputFocusRequester = LocalFocusManager.current
    Card(modifier = Modifier
        .padding(25.dp, 0.dp, 25.dp, 10.dp),
        elevation = 2.dp,
        onClick = { keyboardController?.hide(); inputFocusRequester.clearFocus() }) {
        Column {
            Box(Modifier.weight(5f)) {
                MessageList(messages, history)
            }
            Box(Modifier.weight(1f)) {
                MessageInput()
            }
        }
    }
}


@Composable
fun MessageList(
    messages: SnapshotStateList<Message>,
    history: LazyPagingItems<Message>,
) {

    val scrollState = rememberLazyListState()
    LaunchedEffect(messages.size) {
        if (messages.size === 0) {
            return@LaunchedEffect
        }

        if (scrollState.firstVisibleItemIndex === 0) {
            scrollState.animateScrollToItem(0)
            return@LaunchedEffect
        }

        if (messages.last().type !== MessageType.ME) {
            scrollState.scrollToItem(
                scrollState.firstVisibleItemIndex + 1,
                scrollState.firstVisibleItemScrollOffset
            )
            return@LaunchedEffect
        }
        scrollState.animateScrollToItem(0)
    }

    LazyColumn(
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 2.dp)
            .fillMaxHeight()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom,
        reverseLayout = true,
        state = scrollState
    ) {
        items(messages.reversed()) { message ->
            MessageCard(message)
        }
        items(history) { message ->
            message?.let {
                MessageCard(it)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun MessageInput(viewModel: ChatBoxViewModel = viewModel()) {
    var input by remember { mutableStateOf("") }
    fun sendMessage() {
        if (input.isNotBlank()) {
            viewModel.sendMessage(input)
        }
        input = ""
    }

    Row(
        Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(65.dp),
            value = input,
            onValueChange = { input = it },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = { sendMessage() }
            ),
            placeholder = { Text(text = "Aa") },
            singleLine = true,

            )
        Button(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(55.dp),
            onClick = { sendMessage() },
            enabled = input.isNotBlank()
        ) {
            Text(text = "Send")
        }
    }
}
