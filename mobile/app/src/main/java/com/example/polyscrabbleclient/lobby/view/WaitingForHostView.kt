package com.example.polyscrabbleclient.lobby.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.polyscrabbleclient.lobby.viewmodels.WaitingRoomViewModel
import com.example.polyscrabbleclient.ui.theme.SpinnerView
import com.example.polyscrabbleclient.ui.theme.WaitingForHostResponse
import com.example.polyscrabbleclient.ui.theme.cancelButtonFR
import com.example.polyscrabbleclient.utils.TitleView

@Composable
fun WaitingForHostView(
    navController: NavController,
    viewModel: WaitingRoomViewModel = viewModel()
) {
    CenteredContainer {
        Card {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                TitleView(title = WaitingForHostResponse)
                SpinnerView()
                Button(
                    onClick = { viewModel.leaveLobbyGame(navController) }
                ) {
                    Text(text = cancelButtonFR)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WaitingForHostPreview() {
    WaitingForHostView(rememberNavController())
}
