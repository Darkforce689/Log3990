package com.example.polyscrabbleclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.polyscrabbleclient.auth.components.AuthScreen
import com.example.polyscrabbleclient.auth.viewmodel.AuthenticationViewModel
import com.example.polyscrabbleclient.message.SocketHandler
import com.example.polyscrabbleclient.message.components.ChatRoomScreen
import com.example.polyscrabbleclient.message.model.User
import com.example.polyscrabbleclient.ui.theme.PolyScrabbleClientTheme
import com.example.polyscrabbleclient.message.viewModel.ChatBoxViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SocketHandler.setSocket()
        setContent {
            App {
                Column {
                    NavGraph()
//                    LetterRack()
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
@Preview(showBackground = true)
@Composable
fun NavGraph() {
    val chatView : ChatBoxViewModel = viewModel()
    val loginViewModel : AuthenticationViewModel = viewModel()

    val navController = rememberNavController() // 1
    NavHost(navController, startDestination = "startPage") {
        composable("startPage") { // 2
            Column {
                Prototype(navController)
                Login(navController)
            }
        }
        composable("messageList") {
            // To place in other function somewhere else
            chatView.joinRoom("prototype", User)
            ChatRoomScreen(navController, chatView)
        }
        composable("loginPage"){
            AuthScreen(navController, loginViewModel)
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
fun Login(navController: NavController) {
    Button(modifier = Modifier.padding(20.dp),
        onClick = {
            navController.navigate("loginPage") {
                popUpTo("startPage") {
                    inclusive = true
                }
            }
        }) {
        Text(text = "Login")
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    App {
    }
}

//
//@Composable
//fun Greeting(name: String) {
//    var isSelected by remember {
//        mutableStateOf(false)
//    }
//    val targetColor by animateColorAsState(
//        targetValue = if (isSelected) MaterialTheme.colors.primary else Color.Transparent,
//        animationSpec = tween(durationMillis = 4000)
//    )
//    Surface(color = targetColor) {
//        Text(
//            text = "Hello $name!",
//            modifier = Modifier
//                .clickable { isSelected = !isSelected }
//                .padding(16.dp)
//        )
//    }
//}
//
//@Composable
//fun ScreenContent(names: List<String> = List(3) {"android $it"}) {
//    var countState by remember {
//        mutableStateOf(0)
//    }
//    Column (modifier = Modifier.fillMaxHeight()) {
//        NamesList(names, Modifier.weight(1f))
//        Counter(
//            count = countState,
//            updateCount = { newCount -> countState = newCount}
//        )
//        if (countState > 5) {
//            Text(text = "Count > 5")
//        }
//        LetterRack()
//    }
//}
//
//@Composable
//private fun NamesList(names: List<String>, modifier: Modifier) {
//    LazyColumn(modifier = modifier) {
//        items(items = names) {
//            Greeting(name = it)
//            Divider()
//        }
//    }
//}
//
//@Composable
//fun Counter(count: Int, updateCount: (Int) -> Unit) {
//    Button (onClick = { updateCount(count + 1) } ) {
//        Text(
//            text = "Click: $count",
//        )
//    }
//}
//

