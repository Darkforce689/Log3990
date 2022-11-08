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
import com.example.polyscrabbleclient.message.ChatSocketHandler

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun ChatRoomScreen(navController: NavController, chatBoxViewModel: ChatBoxViewModel) {
    Box {
        Column() {
            Icon(imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .padding(6.dp)
                    .clickable {
                        ChatSocketHandler.closeConnection()
                        chatBoxViewModel.reset()
                        navController.navigate(NavPage.MainPage.label) {
                            popUpTo(NavPage.Room.label) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    })
            ChatBox(chatBoxViewModel = chatBoxViewModel)
        }
    }
}
