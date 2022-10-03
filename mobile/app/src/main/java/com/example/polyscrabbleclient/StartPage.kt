package com.example.polyscrabbleclient

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.polyscrabbleclient.message.model.User
import com.example.polyscrabbleclient.ui.theme.disconnection
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import kotlinx.coroutines.*
import java.net.URL

@Composable
fun StartView(navController: NavController, startViewModel: StartViewModel){
    startViewModel.connectAppSocket()
    Column() {
        Text(text = "Page d'accueil")
        Prototype(navController = navController)
        Disconnection(navController = navController, startViewModel = startViewModel)
    }
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun Prototype(navController: NavController) {
    Button(modifier = Modifier.padding(20.dp),
        onClick = {
            GlobalScope.launch() {
                val updateThread = User.updateUser()
                updateThread.start()
                updateThread.join()
            }
            navController.navigate(NavPage.Room.label) {
                popUpTo(NavPage.MainPage.label) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }) {
        Text(text = "Prototype")
    }
}

@Composable
fun Disconnection(navController: NavController, startViewModel: StartViewModel) {
    Button(modifier = Modifier.padding(20.dp),
        onClick = {
            val thread = Thread {
                ScrabbleHttpClient.get(URL(BuildConfig.COMMUNICATION_URL+"/auth/logout"),String::class.java)
            }
            thread.start()
            thread.join()
            startViewModel.disconnectAppSocket()
            ScrabbleHttpClient.cookieManager.cookieStore.removeAll()
            navController.navigate(NavPage.Login.label) {
                launchSingleTop = true
            }
        }) {
        Text(text = disconnection)
    }
}
