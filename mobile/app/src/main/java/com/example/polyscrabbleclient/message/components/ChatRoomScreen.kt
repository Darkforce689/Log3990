package com.example.polyscrabbleclient.message.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import com.example.polyscrabbleclient.message.model.*
import com.example.polyscrabbleclient.message.viewModel.ChatBoxViewModel
import com.example.polyscrabbleclient.NavPage
import com.example.polyscrabbleclient.message.SocketHandler
import com.example.polyscrabbleclient.ui.theme.NoRippleTheme

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun ChatRoomScreen(navController: NavController,chatBoxViewModel: ChatBoxViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val messages by chatBoxViewModel.messages.observeAsState()
    val inputFocusRequester = LocalFocusManager.current

    Box {
        Column() {
            Icon(imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .padding(6.dp)
                    .clickable {
                        SocketHandler.closeConnection()
                        chatBoxViewModel.reset()
                        navController.navigate(NavPage.MainPage.label) {
                            popUpTo(NavPage.Room.label) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    })
            Card(modifier = Modifier
                .padding(25.dp, 0.dp, 25.dp, 10.dp),
                elevation = 2.dp,
                onClick = { keyboardController?.hide(); inputFocusRequester.clearFocus() }) {
                Column {
                    Box(Modifier.weight(5f)) {
                        MessageList(messages as List<Message>)
                    }
                    Box(Modifier.weight(1f)) {
                        MessageInput()
                    }
                }
            }
        }
    }
}


@Composable
fun MessageList(messages : List<Message>) {
    val scrollState = rememberLazyListState()
    LaunchedEffect(messages.size) {
        scrollState.animateScrollToItem(messages.size)
    }
    LazyColumn(
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 2.dp)
            .fillMaxHeight()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom,
        state = scrollState
    ) {
        items(messages) { message ->
            MessageCard(message)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun MessageInput(viewModel: ChatBoxViewModel = viewModel()) {
    var input by remember { mutableStateOf("") }
    fun sendMessage() {
        if (input.isNotBlank()) {
            viewModel.sendMessage(Message(input, User.name, MessageType.ME, null))
        }
        input = ""
//        keyboardController?.hide()
    }

    Row(
        Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(65.dp),
            value = input,
            onValueChange = { input = it },
            keyboardOptions = KeyboardOptions(keyboardType= KeyboardType.Password, imeAction = ImeAction.Send),
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
