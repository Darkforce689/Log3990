package com.example.polyscrabbleclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.polyscrabbleclient.game.view.LetterRackView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.polyscrabbleclient.game.model.GameModel
import com.example.polyscrabbleclient.game.sources.Player
import com.example.polyscrabbleclient.game.view.GameScreen
import com.example.polyscrabbleclient.game.viewmodels.GameViewModel
import com.example.polyscrabbleclient.ui.theme.PolyScrabbleClientTheme
import com.example.polyscrabbleclient.message.SocketHandler
import com.example.polyscrabbleclient.message.components.ChatRoomScreen
import com.example.polyscrabbleclient.message.model.User
import com.example.polyscrabbleclient.message.viewModel.ChatBoxViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App {
                Column {
                    NavGraph()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SocketHandler.closeConnection()
    }
}

// Main Component
@Composable
fun NavGraph() {
    SocketHandler.setSocket()
    val chatView : ChatBoxViewModel = viewModel()
    val gameViewModel : GameViewModel = viewModel()
    val navController = rememberNavController() // 1
    NavHost(navController, startDestination = "startPage") {
        composable("startPage") { // 2
            Column() {
                Prototype(navController)
                GamePage(navController)
            }
        }
        composable("messageList") {
            // To place in other function somewhere else
            chatView.joinRoom("prototype", User)
            ChatRoomScreen(navController, chatView)
        }
        composable("gamePage") {
            GameScreen(navController, gameViewModel)
        }
    }

}

@Composable
fun Prototype(navController: NavController) {
    Button(modifier = Modifier.padding(20.dp),
        onClick = {
            navController.navigate("messageList") {
                popUpTo("startPage") {
                    inclusive = true
                }
            }
        }) {
        Text(text = "Prototype")
    }
}

@Composable
fun GamePage(navController: NavController) {
    Button(
        modifier = Modifier.padding(20.dp),
        onClick = {
            navController.navigate("gamePage") {
                popUpTo("startPage") {
                    inclusive = true
                }
            }
        }
    )
    {
        Text(text = "GamePageWIP")
    }
}


@Composable
fun App(content: @Composable () -> Unit) {
    PolyScrabbleClientTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            content()
        }
    }
}
