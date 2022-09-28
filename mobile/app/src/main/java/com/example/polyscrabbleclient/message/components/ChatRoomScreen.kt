package com.example.polyscrabbleclient.message.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import com.example.polyscrabbleclient.message.model.*
import com.example.polyscrabbleclient.message.viewModel.ChatBoxViewModel
import com.example.polyscrabbleclient.NavPage
import com.example.polyscrabbleclient.ui.theme.NoRippleTheme

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun ChatRoomScreen(navController: NavController,chatBoxViewModel: ChatBoxViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val messages by chatBoxViewModel.messages.observeAsState()
    val inputFocusRequester = LocalFocusManager.current

    CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme){
        Column {
            Button(modifier = Modifier.padding(20.dp),
                onClick = {
                    chatBoxViewModel.reset()
                    navController.navigate(NavPage.MainPage.label) {
                        popUpTo(NavPage.Room.label) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                    })
            {
                Text("Page d'accueil")
            }
            Card(modifier= Modifier.padding(25.dp, 0.dp, 25.dp, 25.dp),
                elevation = 2.dp,
                onClick = { keyboardController?.hide(); inputFocusRequester.clearFocus()}) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    MessageList(messages as List<Message>)
                    MessageInput()
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
            .fillMaxHeight(0.9f),
        verticalArrangement = Arrangement.Bottom,
        state = scrollState
    ) {
        items(messages) { message ->
            MessageCard(message)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MessageInput(viewModel: ChatBoxViewModel = viewModel()) {
    var input by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val inputFocusRequester = FocusRequester()

    fun sendMessage() {
        if (input.isNotBlank()) {
            viewModel.sendMessage(Message(input, User.name, MessageType.ME, null))
        }
        input = ""
        keyboardController?.hide()
    }


    Row(modifier = Modifier.padding(10.dp, 10.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        OutlinedTextField(
            modifier = Modifier
                .focusRequester(inputFocusRequester)
                .weight(1f),
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
                .padding(3.dp),
            onClick = { sendMessage() },
            enabled = input.isNotBlank()
        ) {
            Text(text = "Send")
        }
    }
}
