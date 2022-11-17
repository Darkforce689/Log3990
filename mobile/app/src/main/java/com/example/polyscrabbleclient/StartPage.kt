package com.example.polyscrabbleclient

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.polyscrabbleclient.page.headerbar.view.HeaderBar
import com.example.polyscrabbleclient.page.headerbar.viewmodels.ThemeSelectorViewModel
import com.example.polyscrabbleclient.ui.theme.disconnection
import com.example.polyscrabbleclient.ui.theme.gamePage
import com.example.polyscrabbleclient.ui.theme.new_game
import com.example.polyscrabbleclient.user.User
import com.example.polyscrabbleclient.utils.PageSurface
import com.example.polyscrabbleclient.utils.PhysicalButtons
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL

@Composable
fun StartView(
    navController: NavController,
    startViewModel: StartViewModel,
) {
    PhysicalButtons.reset()
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        GamePage(navController = navController)
        NewGamePage(navController)
        Disconnection(navController = navController, startViewModel = startViewModel)
    }
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun Prototype(navController: NavController) {
    Button(modifier = Modifier.padding(20.dp),
        onClick = {
            GlobalScope.launch {
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
                ScrabbleHttpClient.get(
                    URL(BuildConfig.COMMUNICATION_URL + "/auth/logout"),
                    String::class.java
                )
            }
            thread.start()
            thread.join()
            startViewModel.disconnectAppSocket()
            ScrabbleHttpClient.clearCookies()
            User.resetUserInfo()
            navController.navigate(NavPage.Login.label) {
                popUpTo(NavPage.MainPage.label) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }) {
        Text(text = disconnection)
    }
}

@Composable
fun GamePage(navController: NavController) {
    Button(
        modifier = Modifier.padding(20.dp),
        onClick = {
            navController.navigate(NavPage.GamePage.label) {
                popUpTo(NavPage.MainPage.label) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    )
    {
        Text(text = gamePage)
    }
}

@Composable
fun NewGamePage(navController: NavController) {
    Button(
        modifier = Modifier.padding(20.dp),
        onClick = {
            navController.navigate(NavPage.NewGame.label) {
                launchSingleTop = true
            }
        }
    )
    {
        Text(text = new_game)
    }
}
