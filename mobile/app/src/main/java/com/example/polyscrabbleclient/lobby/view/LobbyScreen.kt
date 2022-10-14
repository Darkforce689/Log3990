package com.example.polyscrabbleclient.lobby.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.polyscrabbleclient.game.view.draganddrop.DragState
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel
import com.example.polyscrabbleclient.lobby.viewmodels.LobbyViewModel
import kotlinx.coroutines.delay



@Composable
fun LobbyScreen(navController: NavController?) {
    val viewModel: LobbyViewModel = viewModel()

    EvenlySpacedRowContainer {
        Box {
            Text("A")
        }
    }
}

//@Composable
//fun EvenlySpacedSubColumn(content: @Composable ColumnScope.() -> Unit) {
//    Column (
//        modifier = Modifier.fillMaxHeight(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.SpaceEvenly
//    ) {
//        content()
//    }
//}

@Composable
fun EvenlySpacedRowContainer(content: @Composable RowScope.() -> Unit) {
    Row (
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        content()
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
fun LobbyScreenPreview() {
    LobbyScreen(null)
}
