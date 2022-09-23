package com.example.polyscrabbleclient

import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.polyscrabbleclient.message.SocketHandler
import com.example.polyscrabbleclient.message.components.ChatRoomScreen
import com.example.polyscrabbleclient.message.model.User
import com.example.polyscrabbleclient.message.viewModel.ChatBoxViewModel
import com.example.polyscrabbleclient.ui.theme.PolyScrabbleClientTheme
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import com.google.gson.Gson
import java.io.BufferedInputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.*
import java.nio.charset.Charset

data class Credentials(val email: String, val password: String)
data class Score(val _id: String, val score: Int, val name: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val url = URL(BuildConfig.COMMUNICATION_URL + "/auth/login")
        val scoreUrl = URL(BuildConfig.API_URL + "/scores/classic")

        val thread = Thread {

            val creds = Credentials("olivier1@gmail.com", "password")
            data class AuthRes(val errors: List<String>?, val message: String?)
            val authRes = ScrabbleHttpClient.post(url, creds, AuthRes::class.java)

            println("auth res" + authRes.toString())

            val scores: Array<Score>? = ScrabbleHttpClient.get(scoreUrl, Array<Score>::class.java)
            println(scores.toString())

            SocketHandler.setSocket()

        }

        thread.start();



        super.onCreate(savedInstanceState)
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
@Composable
fun NavGraph() {

    val chatView : ChatBoxViewModel = viewModel()
    val navController = rememberNavController() // 1
    NavHost(navController, startDestination = "startPage") {
        composable("startPage") { // 2
            Column() {
                Prototype(navController)
            }
        }
        composable("messageList") {
            // To place in other function somewhere else
            chatView.joinRoom("prototype")
            ChatRoomScreen(navController, chatView)
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

