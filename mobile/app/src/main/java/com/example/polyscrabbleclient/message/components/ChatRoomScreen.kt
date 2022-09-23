package com.example.polyscrabbleclient.message.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
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
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.polyscrabbleclient.message.model.*
import com.example.polyscrabbleclient.message.viewModel.ChatBoxViewModel
import com.example.polyscrabbleclient.navPage

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun ChatRoomScreen(navController: NavController,chatBoxViewModel: ChatBoxViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val messages by chatBoxViewModel.messages.observeAsState()

    CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme){
        Column {
            Button(modifier = Modifier.padding(20.dp),
                onClick = {
                    navController.navigate(navPage.Start.label) {
                        popUpTo(navPage.Room.label) {
                            inclusive = true
                        }
                    }
                    })
            {
                Text("Page d'accueil")
            }
            Card(modifier= Modifier.padding(25.dp, 10.dp, 25.dp, 25.dp),
                shape = RoundedCornerShape(13.dp),
                elevation = 5.dp,
                backgroundColor = Color.LightGray,
                onClick = { keyboardController?.hide()}) {
                    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                    verticalArrangement = Arrangement.Bottom) {
                        MessageList(messages as List<Message>)
                        MessageInput()}
            }
        }
    }

}


@Composable
fun MessageList(messages : List<Message>) {
    Column(modifier = Modifier
        .padding(vertical = 4.dp, horizontal = 2.dp).fillMaxHeight(0.9f)
        .verticalScroll(rememberScrollState(), true,null,true),
        verticalArrangement = Arrangement.Bottom)
     {
        messages.forEach { message ->
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
        if(input.isNotBlank()) {
            viewModel.sendMessage(Message(input, User.name, MessageType.ME ))
        }
        input = ""
        keyboardController?.hide()
    }

    Row(modifier = Modifier.padding(20.dp).focusRequester(inputFocusRequester), horizontalArrangement = Arrangement.SpaceAround){
        OutlinedTextField(modifier = Modifier
            .weight(1f),
            value = input ,
            onValueChange = { input = it},
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(
                onSend = { sendMessage()}
            ),
            placeholder = { Text(text = "Aa") },
            singleLine = true,
        )
        Button(
            modifier = Modifier
                .padding(4.dp),
            onClick = { sendMessage() },
            enabled = input.isNotBlank()
        ) {
            Text(text = "Send")
        }
    }
}

private object NoRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = Color.Unspecified

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleAlpha(0.0f,0.0f,0.0f,0.0f)
}
